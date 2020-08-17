package baliviya.com.github.eduBot.dao.impl;

import baliviya.com.github.eduBot.dao.AbstractDao;
import baliviya.com.github.eduBot.entity.custom.Document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DocumentDao extends AbstractDao<Document> {
	
	
	public void insert(Document doc) {
		sql = "INSERT INTO DOCUMENT_FILES(NAME, DOCUMENT, CHAT_ID, BUTTON_ID) VALUES (?, ?, ?, ?)";
		getJdbcTemplate().update(sql, doc.getName(), doc.getDocument(), doc.getChat_id(), doc.getButton_id());
	}
	
	public void update(Document document) {
		sql = "UPDATE DOCUMENT_FILES SET NAME = ?, DOCUMENT = ?";
		getJdbcTemplate().update(sql, document.getName(), document.getDocument());
	}
	
	public List<Document> getAllDocumentsByButtonId(int id){
		sql = "SELECT * FROM DOCUMENT_FILES WHERE BUTTON_ID = ?";
		return getJdbcTemplate().query(sql,setParam(id),this::mapper);
	}
	
//	public List<Document> getAll() {
//		sql = "SELECT * FROM DOCUMENT_FILES";
//		return getJdbcTemplate().query(sql, this::mapper);
//	}
	
//	public Document getDocumentByFileName(String file_name) {
//		sql = "SELECT * FROM DOCUMENT_FILES WHERE NAME = ? ";
//		return getJdbcTemplate().queryForObject(sql, setParam(file_name), this::mapper);
//	}
	public boolean isContains(String fileName){
		sql = "SELECT count(*) FROM DOCUMENT_FILES WHERE NAME = ?";
		return getJdbcTemplate().queryForObject(sql,setParam(fileName), Integer.class) > 0;
	}
	
	@Override
	protected Document mapper(ResultSet rs, int index) throws SQLException {
		
		Document document = new Document();
		document.setId(rs.getInt(1));
		document.setName(rs.getString(2));
		document.setDocument(rs.getString(3));
		return document;
	}
}
