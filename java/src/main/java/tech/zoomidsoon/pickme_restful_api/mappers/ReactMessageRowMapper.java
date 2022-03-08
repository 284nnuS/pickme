package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.ReactMessage;

public class ReactMessageRowMapper extends RowMapper<ReactMessage> {

	private static final RowMapper<ReactMessage> singleton = new ReactMessageRowMapper();

	public ReactMessageRowMapper() {
	}

	public static RowMapper<ReactMessage> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, ReactMessage obj) throws SQLException {
		Integer messageId = rs.getInt("messageId");

		if (!obj.isEmpty() && messageId != null && messageId != obj.getMessageId())
			return true;

		if (obj.isEmpty()) {
			obj.setMessageId(messageId);
			obj.setReact(rs.getString("react"));
		}

		return false;
	}

}
