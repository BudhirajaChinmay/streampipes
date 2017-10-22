package org.streampipes.model.impl.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass("sepa:SECInvocationGraph")
@Entity
public class SecInvocation extends InvocableSEPAElement {

	private static final long serialVersionUID = -2345635798917416757L;
		
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:ecType")
	protected List<String> category;

	public SecInvocation(SecInvocation sec) {
		super(sec);
		this.category = sec.getCategory();

	}

	public SecInvocation(SecDescription sec)
	{
		super();
		this.setName(sec.getName());
		this.setDescription(sec.getDescription());
		this.setIconUrl(sec.getIconUrl());
		this.setInputStreams(sec.getEventStreams());
		this.setSupportedGrounding(sec.getSupportedGrounding());
		this.setStaticProperties(sec.getStaticProperties());
		this.setBelongsTo(sec.getElementId().toString());
		this.category = sec.getCategory();
		this.setStreamRequirements(sec.getEventStreams());
		//this.setUri(belongsTo +"/" +getElementId());
	}
	
	public SecInvocation(SecDescription sec, String domId)
	{
		this(sec);
		this.setDOM(domId);
	}
	
	public SecInvocation()
	{
		super();
		inputStreams = new ArrayList<EventStream>();
	}
	
	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}
		
}
