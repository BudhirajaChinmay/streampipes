package de.fzi.cep.sepa.flink.samples.count.aggregate;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.commons.collections.map.HashedMap;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CountAggregateProgram extends FlinkSepaRuntime<CountAggregateParameters>{

	private static String AGGREGATE_COUNT = "aggregate_taxi_count";
	public CountAggregateProgram(CountAggregateParameters params) {
		super(params);
		this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
	}

	public CountAggregateProgram(CountAggregateParameters params, FlinkDeploymentConfig config) {
		super(params, config);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {

//		this.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		List<String> groupBy = params.getGroupBy();

		DataStream<Map<String, Object>> result = messageStream[0]
//                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Map<String, Object>>() {
//
//					@Override
//					public long extractAscendingTimestamp(Map<String, Object> element) {
//					    System.out.println(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format((long) element.get("tpep_pickup_datetime")));
//						return (long) element.get("tpep_pickup_datetime");
//					}
//				})
				.map(new MapFunction<Map<String, Object>, Tuple2<String, Map<String, Object>>>() {
					@Override
					public Tuple2<String, Map<String, Object>> map(Map<String, Object> value) throws Exception {

						String newKey = "";

						for (String s : groupBy) {
							newKey = newKey + value.get(s).toString();
						}

						return new Tuple2<String, Map<String, Object>>(newKey, value);
					}
				})
				.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple2<String, Map<String, Object>>>() {

					@Override
					public long extractAscendingTimestamp(Tuple2<String, Map<String, Object>> element) {
						return (long) element.f1.get("tpep_pickup_datetime");
					}
				})
				.keyBy(0)
				.timeWindow(params.getTimeWindowSize(), params.getSlideWindowSize())
				.apply(new MyWindow2Function())
				.map(new MapFunction< Tuple2<String, Map<String, Object>>, Map<String, Object>>() {
					@Override
					public Map<String, Object> map(Tuple2<String, Map<String, Object>> value) throws Exception {
						return value.f1;
					}
				});

		return result;
	}

	private static class MyWindow2Function implements WindowFunction<Tuple2<String,Map<String,
			Object>>, Tuple2<String, Map<String, Object>>, Tuple, TimeWindow> {

		@Override
		public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<Tuple2<String, Map<String, Object>>> iterable,
						  Collector<Tuple2<String, Map<String, Object>>> collector) throws Exception {

			Map<String, Object> result = new HashMap<>();
			String key = "";
			Iterator<Tuple2<String, Map<String, Object>>> iterator = iterable.iterator();
			int count = 0;
			long passengerCount = 0;
			double tripDistance = 0;
			double fareAmount = 0;
			double extra = 0;
			double tip_amount = 0;
			double tolls_amount = 0;
			double total_amount = 0;
			int[] rateCodeIdValues = {0, 0, 0, 0, 0, 0};
			int[] paymentTypeValues = {0, 0, 0, 0, 0, 0};
			int mtaTaxCount = 0;
			int improvementSurchargeCount = 0;


			while(iterator.hasNext()) {
				Tuple2<String, Map<String, Object>> tmp = iterator.next();
				count++;

				passengerCount += (int) tmp.f1.get("passenger_count");
				tripDistance += toDouble(tmp.f1.get("trip_distance"));
				fareAmount += toDouble(tmp.f1.get("fare_amount"));
				extra += toDouble(tmp.f1.get("extra"));
				tip_amount += toDouble(tmp.f1.get("tip_amount"));
				tolls_amount += toDouble(tmp.f1.get("tolls_amount"));
				total_amount += toDouble(tmp.f1.get("total_amount"));

				int rateCodeId = (int) tmp.f1.get("ratecode_id");
				rateCodeIdValues[rateCodeId - 1] += 1;

				int paymentType = (int) tmp.f1.get("payment_type");
				paymentTypeValues[paymentType - 1] += 1;

				double mtaTax = toDouble(tmp.f1.get("mta_tax"));
				if (mtaTax == 0.5) {
					mtaTaxCount++;
				}

				double improvementSurcharge = toDouble(tmp.f1.get("improvement_surcharge"));
				if (improvementSurcharge == 0.3) {
					improvementSurchargeCount++;
				}


				result.put("vendor_id", tmp.f1.get("vendor_id"));
				key = tmp.f0;
			}

			double passengerCountAvg = ((double) passengerCount) / count;
			double tripDistanceAvg = ((double) tripDistance) / count;
			double fareAmountAvg = ((double) fareAmount) / count;
			double extraAvg = ((double) extra) / count;
			double tip_amountAvg = ((double) tip_amount) / count;
			double tolls_amountAvg = ((double) tolls_amount) / count;
			double total_amountAvg = ((double) total_amount) / count;

			result.put("window_time_start", timeWindow.getStart());
			result.put("window_time_end", timeWindow.getEnd());

			result.put("passenger_count_avg", passengerCountAvg);

			result.put("trip_distance_avg", tripDistanceAvg);
			result.put("fare_amount_avg", fareAmountAvg);
			result.put("extra_avg", extraAvg);
			result.put("tip_amount_avg", tip_amountAvg);
			result.put("tolls_amount_avg", tolls_amountAvg);
			result.put("total_amount_avg", total_amountAvg);

			// Rate code count
			result.put("rate_code_id_1", rateCodeIdValues[0]);
			result.put("rate_code_id_2", rateCodeIdValues[1]);
			result.put("rate_code_id_3", rateCodeIdValues[2]);
			result.put("rate_code_id_4", rateCodeIdValues[3]);
			result.put("rate_code_id_5", rateCodeIdValues[4]);
			result.put("rate_code_id_6", rateCodeIdValues[5]);

			//Payment type count
			result.put("payment_type_1", paymentTypeValues[0]);
			result.put("payment_type_2", paymentTypeValues[1]);
			result.put("payment_type_3", paymentTypeValues[2]);
			result.put("payment_type_4", paymentTypeValues[3]);
			result.put("payment_type_5", paymentTypeValues[4]);
			result.put("payment_type_6", paymentTypeValues[5]);

			result.put("mta_tax", mtaTaxCount);
			result.put("improvement_surcharge", improvementSurchargeCount);


			result.put(AGGREGATE_COUNT, count);

//			System.out.println("============================================");
//			System.out.println("Window start: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timeWindow.getStart()));
//            System.out.println(result);
//			System.out.println("Window end: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timeWindow.getEnd()));
//			System.out.println("============================================");



			collector.collect(new Tuple2<String, Map<String, Object>>(key, result));
		}
	}

	private static double toDouble(Object o) {
		double result;
		if (o instanceof Integer) {
		    result = (Integer) o;
		} else {
			result = (double) o;

		}

		return result;
	}

}
