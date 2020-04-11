package com.noovitec.mpb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingDto {

	private String fileStoreDir = "/home/koziarskim/mpb/mpb_file_store";
	private boolean skipNotification = false;
	
}
