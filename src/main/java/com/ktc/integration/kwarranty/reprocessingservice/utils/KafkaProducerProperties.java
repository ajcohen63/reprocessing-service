package com.ktc.integration.kwarranty.reprocessingservice.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component

@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {

	private Map<String, String> props;

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}

	public Properties asProperties() {
		Properties props = null;
		try 
		{
			if(this.props == null) {
				System.out.println("ERROR !! No kafka producer properties found.\n");
			} 
			else 
			{
				props = new Properties();
				for (Map.Entry<String, ?> entry : this.props.entrySet()) {
					Object v = entry.getValue();
					if (v != null) {
						props.put(entry.getKey().replaceAll("-", "."), v.toString());
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error while loading kafka properties.\n");
		}
		return props;
	}

}
