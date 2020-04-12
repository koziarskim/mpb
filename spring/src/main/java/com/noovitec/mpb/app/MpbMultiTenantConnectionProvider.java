package com.noovitec.mpb.app;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MpbMultiTenantConnectionProvider implements MultiTenantConnectionProvider {
     
	private static final long serialVersionUID = 1L;
	@Autowired
    private DataSource dataSource;
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }
    @Override
    public Connection getConnection(String tenantIdentifie) throws SQLException {
        String tenantIdentifier = MpbTenantContext.getCurrentTenant();
        final Connection connection = getAnyConnection();
        try {
            if (tenantIdentifier == null) {
            	throw new HibernateException("Schema tenant not specified");
            }             	
            connection.createStatement().execute("SET SCHEMA '" + tenantIdentifier + "'"); 
        }
        catch ( SQLException e ) {
            throw new HibernateException("Problem setting schema to " + tenantIdentifier, e);
        }
        return connection;
    }
    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.createStatement().execute( "SET SCHEMA '"+tenantIdentifier+"'" );
        }
        catch ( SQLException e ) {
            throw new HibernateException("Problem setting schema to " + tenantIdentifier, e);
        }
        connection.close();
    }
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }
    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
}