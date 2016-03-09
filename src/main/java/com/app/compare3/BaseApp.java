package com.app.compare3;

import java.io.File;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class BaseApp {
	protected void iniFileListInDirs(List<File> fileList,String... dirs) throws Exception {
		for (String dir : dirs) {
			iniFileListInDir(fileList,new File(dir));
		}
	}
	
	protected void iniFileListInDir(List<File> fileList, File dir) throws Exception {
		if (dir.getAbsolutePath().contains("webapp")) {
			log.warn("【跳过该文件：】" + dir.getName());
			return;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				iniFileListInDir(fileList, file);
			} else {
				fileList.add(file);
			}
		}
	}
	
	
	protected String pathDeal(String absolutePath) {
		return absolutePath.replace("\\", "/");
	}
}
