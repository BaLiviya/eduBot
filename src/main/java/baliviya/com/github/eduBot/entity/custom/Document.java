package baliviya.com.github.eduBot.entity.custom;

import lombok.Data;

@Data
public class Document {
	
	private int id;
	private String name;
	private String document;
	private long chat_id;
	private int button_id;
}
