/*
 * Copyright (c) 2001-2011 Convertigo SA.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 *
 * $URL: $
 * $Author: $
 * $Revision: $
 * $Date: $
 */

package com.twinsoft.convertigo.beans.connectors;

import com.twinsoft.convertigo.beans.core.Connector;
import com.twinsoft.convertigo.beans.core.ConnectorEvent;
import com.twinsoft.convertigo.beans.core.Transaction;
import com.twinsoft.convertigo.beans.transactions.couchdb.AbstractCouchDbTransaction;
import com.twinsoft.convertigo.beans.transactions.couchdb.AbstractDatabaseTransaction;
import com.twinsoft.convertigo.engine.Context;
import com.twinsoft.convertigo.engine.Engine;
import com.twinsoft.convertigo.engine.EngineException;
import com.twinsoft.convertigo.engine.providers.couchdb.CouchDbProperties;
import com.twinsoft.convertigo.engine.providers.couchdb.CouchDbProvider;
import com.twinsoft.convertigo.engine.util.ParameterUtils;

public class CouchDbConnector extends Connector {

	private static final long serialVersionUID = -8895252401444085569L;

	private String databaseName = "";
	private String server = "127.0.0.1";
	private int port = 5984;
	private boolean https = false;
	
	private transient CouchDbProvider dbClient = null;
	
	public CouchDbConnector() {
		
	}

	@Override
	public Connector clone() throws CloneNotSupportedException {
		CouchDbConnector clonedObject = (CouchDbConnector) super.clone();
		clonedObject.dbClient = dbClient;
		return clonedObject;
	}

	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName the databaseName to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the https
	 */
	public boolean isHttps() {
		return https;
	}

	/**
	 * @param https the https to set
	 */
	public void setHttps(boolean https) {
		this.https = https;
	}
	
	@Override
	public void release() {
		super.release();

		try {
			if (dbClient != null) {
				dbClient.shutdown();
				Engine.logBeans.debug("(CouchDbConnector) Connector released");
			}
        } catch (Exception ee) {
			Engine.logBeans.error("(CouchDbConnector) An error occured while releasing connector", ee);
		}
		finally {
			dbClient = null;
		}
		
	}

	public CouchDbProvider getCouchDbClient() {
		if (dbClient == null) {
			String dbName = getDatabaseName();
			
			CouchDbProperties properties = new CouchDbProperties()
			  .setDbName(dbName)
			  .setCreateDbIfNotExist(dbName.isEmpty() ? false:true)
			  .setProtocol(isHttps() ? "https":"http")
			  .setHost(getServer())
			  .setPort(getPort())
			  .setMaxConnections(10);
			
			dbClient = new CouchDbProvider(properties);
		}
		return dbClient;
	}
	
	public void setCouchDbClient(CouchDbProvider dbClient) {
		this.dbClient = dbClient;
	}
	
	public void setData(Object data) {
		fireDataChanged(new ConnectorEvent(this, data));
	}
	
	@Override
	public void prepareForTransaction(Context context) throws EngineException {
		AbstractCouchDbTransaction couchDbTransaction = (AbstractCouchDbTransaction) context.requestedObject;
		
		// Set the target database
		if (couchDbTransaction instanceof AbstractDatabaseTransaction) {
			String targetDbName = ParameterUtils.toString(couchDbTransaction.getParameterValue(AbstractDatabaseTransaction.var_database));
			if (targetDbName == null /*|| targetDbName.isEmpty()*/) {
				targetDbName = getDatabaseName();
			}
			((AbstractDatabaseTransaction)couchDbTransaction).setTargetDatabase(targetDbName);
		}
	}

	@Override
	public Transaction newTransaction() {
		return null;
	}

}