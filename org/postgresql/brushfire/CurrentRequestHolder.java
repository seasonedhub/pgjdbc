package org.postgresql.brushfire;

import javax.servlet.http.HttpServletRequest;

public interface CurrentRequestHolder {

	HttpServletRequest getServletRequest();

	String getAmazonTraceId();

}
