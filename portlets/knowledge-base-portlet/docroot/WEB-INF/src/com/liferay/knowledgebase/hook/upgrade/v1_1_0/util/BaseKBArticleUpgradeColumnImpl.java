/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.knowledgebase.hook.upgrade.v1_1_0.util;

import com.liferay.knowledgebase.model.KBArticle;
import com.liferay.knowledgebase.service.KBArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.upgrade.util.BaseUpgradeColumnImpl;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class BaseKBArticleUpgradeColumnImpl
	extends BaseUpgradeColumnImpl {

	public BaseKBArticleUpgradeColumnImpl(String name) {
		super(name);
	}

	protected KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws Exception {

		KBArticle kbArticle = KBArticleLocalServiceUtil.createKBArticle(0);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(3);

			sb.append("select * from KBArticle where resourcePrimKey = ?");

			if (status != WorkflowConstants.STATUS_ANY) {
				sb.append(" and status = ?");
			}

			sb.append(" order by version desc");

			ps = con.prepareStatement(sb.toString());

			ps.setLong(1, resourcePrimKey);

			if (status != WorkflowConstants.STATUS_ANY) {
				ps.setInt(2, status);
			}

			rs = ps.executeQuery();

			if (rs.next()) {
				kbArticle.setKbArticleId(rs.getLong("kbArticleId"));
				kbArticle.setResourcePrimKey(rs.getLong("resourcePrimKey"));
				kbArticle.setGroupId(rs.getLong("groupId"));
				kbArticle.setCompanyId(rs.getLong("companyId"));
				kbArticle.setParentResourcePrimKey(
					rs.getLong("parentResourcePrimKey"));
				kbArticle.setVersion(rs.getInt("version"));
				kbArticle.setStatus(rs.getInt("status"));
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		return kbArticle;
	}

}
