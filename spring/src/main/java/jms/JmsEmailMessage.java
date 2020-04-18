package jms;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.noovitec.mpb.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JmsEmailMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String> emails;
	Map<String, String> model;
	Notification.TYPE type;
	String tenant;
}
