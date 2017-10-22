package org.streampipes.model.impl.quality;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.UnnamedSEPAElement;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass("sepa:EventStreamQualityRequirement")
@MappedSuperclass
@Entity
public class EventStreamQualityRequirement extends UnnamedSEPAElement {

	private static final long serialVersionUID = 1484115035721357275L;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:minimumEventStreamQuality")
	transient EventStreamQualityDefinition minimumStreamQuality;

	@OneToOne(cascade = {CascadeType.ALL})
	@RdfProperty("sepa:maximumEventStreamQuality")
	transient EventStreamQualityDefinition maximumStreamQuality;

	public EventStreamQualityRequirement(EventStreamQualityDefinition minimumStreamQuality,
			EventStreamQualityDefinition maximumStreamQuality) {
		super();
		//TODO check that minimum and maximum have the same type

		this.minimumStreamQuality = minimumStreamQuality;
		this.maximumStreamQuality = maximumStreamQuality;
	}
	
	public EventStreamQualityRequirement(EventStreamQualityRequirement other) {
		super(other);
		//this.minimumStreamQuality = other.getMinimumStreamQuality();
		//this.maximumStreamQuality = other.getMaximumStreamQuality();
	}
	
	public EventStreamQualityRequirement() {
		super();
	}

	public EventStreamQualityDefinition getMinimumStreamQuality() {
		return minimumStreamQuality;
	}

	public void setMinimumStreamQuality(EventStreamQualityDefinition minimumStreamQuality) {
		this.minimumStreamQuality = minimumStreamQuality;
	}

	public EventStreamQualityDefinition getMaximumStreamQuality() {
		return maximumStreamQuality;
	}

	public void setMaximumStreamQuality(EventStreamQualityDefinition maximumStreamQuality) {
		this.maximumStreamQuality = maximumStreamQuality;
	}
}
