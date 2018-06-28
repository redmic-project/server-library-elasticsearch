package es.redmic.es.common.service;

import java.util.List;

public interface UserUtilsServiceItfc {

	public String getUserId();

	public List<String> getUserRole();

	public List<Long> getAccessibilityControl();
}
