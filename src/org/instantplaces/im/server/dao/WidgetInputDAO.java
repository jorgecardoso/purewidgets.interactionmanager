package org.instantplaces.im.server.dao;

import java.util.Arrays;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
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
	private String userId;


	@Unindexed
	private String nickname;


	@Indexed
	private long timeStamp;

	@Id
	private Long widgetInputId;

//	@NotSaved
//	private WidgetOptionDao widgetOption;

	@Parent
	private Key<WidgetOptionDaot> widgetOptionKey;

	public WidgetInputDao(Key<WidgetOptionDaot> parent, long timeStamp, String[] parameters,
			String userId, String nickname) {
		this.timeStamp = timeStamp;
		this.parameters = parameters;
		this.userId = userId;
		this.nickname = nickname;
		this.delivered = false;
		this.widgetOptionKey = parent;
	}

//	public void setWidgetOptionDSO(WidgetOptionDao widgetOption) {
//		this.widgetOption = widgetOption;
//		if (null != widgetOption) {
//
//			this.widgetOptionKey = new Key<WidgetOptionDao>(widgetOption.getWidgetKey(),
//					WidgetOptionDao.class, widgetOption.getWidgetOptionId());
//		}
//	}
	
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


	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return the widgetInputId
	 */
	public Long getWidgetInputId() {
		return widgetInputId;
	}

//	public WidgetOptionDao getWidgetOptionDSO() {
//		return this.widgetOption;
//	}

	/**
	 * @return the widgetOptionKey
	 */
	public Key<WidgetOptionDaot> getWidgetOptionKey() {
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

	

	/**
	 * @param widgetOptionKey
	 *            the widgetOptionKey to set
	 */
	public void setWidgetOptionKey(Key<WidgetOptionDaot> widgetOptionKey) {
		this.widgetOptionKey = widgetOptionKey;
	}

	@Override
	public String toString() {

		return "WidgetInput: " + "nickname: " + this.nickname + "; parameters "
				+ Arrays.toString(this.parameters) + ")";
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
