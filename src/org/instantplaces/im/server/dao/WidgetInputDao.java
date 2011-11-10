package org.instantplaces.im.server.dao;

import java.util.Arrays;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

public class WidgetInputDao {

	/**
	 * Indicates whether this input has already been delivered to the owner
	 * application
	 */
	@Unindexed
	private boolean delivered;

	/**
	 * The input mechanism that was used to generate this input
	 */
	@Unindexed
	private String inputMechanism;

	@Unindexed
	private String[] parameters;

	@Unindexed
	private String persona;

	@Indexed
	private long timeStamp;

	@Id
	private Long widgetInputId;

	@NotSaved
	private WidgetOptionDao widgetOption;

	@Parent
	private Key<WidgetOptionDao> widgetOptionKey;

	public WidgetInputDao(WidgetOptionDao widgetOptionDso, long timeStamp, String[] parameters,
			String persona) {
		this.timeStamp = timeStamp;
		this.parameters = parameters;
		this.persona = persona;
		this.delivered = false;
		this.setWidgetOptionDSO(widgetOptionDso);
	}

	@SuppressWarnings("unused")
	private WidgetInputDao() {

	}

	/**
	 * @return the inputMechanism
	 */
	public String getInputMechanism() {
		return inputMechanism;
	}

	public String[] getParameters() {
		return parameters;
	}

	public String getPersona() {
		return persona;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return the widgetInputId
	 */
	public Long getWidgetInputId() {
		return widgetInputId;
	}

	public WidgetOptionDao getWidgetOptionDSO() {
		return this.widgetOption;
	}

	/**
	 * @return the widgetOptionKey
	 */
	public Key<WidgetOptionDao> getWidgetOptionKey() {
		return widgetOptionKey;
	}

	/**
	 * @return the delivered
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * @param delivered
	 *            the delivered to set
	 */
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	/**
	 * @param inputMechanism
	 *            the inputMechanism to set
	 */
	public void setInputMechanism(String inputMechanism) {
		this.inputMechanism = inputMechanism;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @param widgetInputId
	 *            the widgetInputId to set
	 */
	public void setWidgetInputId(Long widgetInputId) {
		this.widgetInputId = widgetInputId;
	}

	public void setWidgetOptionDSO(WidgetOptionDao widgetOption) {
		this.widgetOption = widgetOption;
		if (null != widgetOption) {

			this.widgetOptionKey = new Key<WidgetOptionDao>(widgetOption.getWidgetKey(),
					WidgetOptionDao.class, widgetOption.getWidgetOptionId());
		}
	}

	/**
	 * @param widgetOptionKey
	 *            the widgetOptionKey to set
	 */
	public void setWidgetOptionKey(Key<WidgetOptionDao> widgetOptionKey) {
		this.widgetOptionKey = widgetOptionKey;
	}

	@Override
	public String toString() {

		return "WidgetInput: " + "persona: " + this.persona + "; parameters "
				+ Arrays.toString(this.parameters) + ")";
	}

}
