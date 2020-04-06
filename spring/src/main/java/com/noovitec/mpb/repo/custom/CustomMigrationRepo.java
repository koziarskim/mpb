package com.noovitec.mpb.repo.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.service.ItemService;

public interface CustomMigrationRepo {
	public void createTenant(String tenantFrom, String tenantTo) throws IOException;

	@Repository
	public class CustomMigrationRepoImpl implements CustomMigrationRepo {

		private final Logger log = LoggerFactory.getLogger(CustomMigrationRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;
		@Autowired
		ItemService itemService;

		public void createTenant(String tenantFrom, String tenantTo) throws IOException {
			this.createSchema(tenantFrom, tenantTo);
			this.transferTenantData(tenantFrom, tenantTo);
		}

		private void createSchema( String tenantFrom, String tenantTo) throws IOException {
	        try {
	        	String fileContent = "create schema "+tenantTo+";\n";
				fileContent += this.getFileContent("sql/year_schema.sql", tenantFrom, tenantTo);
				entityManager.createNativeQuery(fileContent).executeUpdate();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("Cannot create schema for tenant: "+tenantTo, e);
			}
		}

		private String getFileContent(String filePath, String schemaFrom, String schemaName) throws IOException {
			String fileContent = "";
	        InputStreamReader fileIsr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filePath), StandardCharsets.UTF_8);
	        BufferedReader fileBr = new BufferedReader(fileIsr);
	        String currentLine;
			while ((currentLine = fileBr.readLine()) != null) {
				if(currentLine.startsWith("--") || 
						currentLine.startsWith("SET ") ||
						currentLine.startsWith("CREATE DATABASE ") ||
						currentLine.startsWith("ALTER DATABASE ") ||
						currentLine.startsWith("\\connect ") ||
						currentLine.startsWith("REVOKE ALL ") ||
						currentLine.startsWith("GRANT ALL ") ||
						currentLine.startsWith("SELECT pg_catalog.set_config")
					) {
					continue;
				}
				currentLine = currentLine.replace("::regclass", "");
				currentLine = currentLine.replace("::character varying","");
				currentLine = currentLine.replace("schemaName", schemaName);
				currentLine = currentLine.replace("schemaFrom", schemaFrom);
				fileContent += currentLine+"\n";
			}
			return fileContent;
		}
		
		private void transferTenantData(String tenantFrom, String tenantTo) throws IOException {
        	List<String> tables = Arrays.asList("address", "attachment", "supplier", "component", "customer", "customer_address", 
        			"item", "item_component");
        	for(String table: tables) {
        		String q = "INSERT INTO "+tenantTo+"."+table+" SELECT * FROM "+tenantFrom+"."+table;
        		entityManager.createNativeQuery(q).executeUpdate();
        	}
        	tables = Arrays.asList("address", "attachment", "supplier", "component", "customer", "item", "item_component");
        	for(String table: tables) {
        		String q = "SELECT MAX(id) from "+tenantFrom+"."+table;
        		Object result = entityManager.createNativeQuery(q).getSingleResult();
        		int seqNumber = result==null?1:((BigInteger) result).intValue();
        		q = "SELECT pg_catalog.setval('"+tenantTo+"."+table+"_id_seq',"+seqNumber+", true)";
        		entityManager.createNativeQuery(q).getSingleResult();
        	}

		}
	}
}