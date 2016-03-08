package com.app.compare;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.util.FileEncodeCheckUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class FileCompareStrategyByFileContent implements FileCompareStrategy {
	static int count = 1;

	@Override
	public boolean isEqualsTwoFile(File file1, File file2) throws Exception {
		if (!FileEncodeCheckUtil.get_charset(file1).equals(FileEncodeCheckUtil.get_charset(file2))) {
			log.info("文件编码不一致：比较,源文件:" + file1 +"-->>生产SVN："+FileEncodeCheckUtil.get_charset(file1)+ "目标文件:" + "-->>开发SVN："+FileEncodeCheckUtil.get_charset(file2));
		}
		List<String> fileContentsFile1 = FileUtils.readLines(file1,FileEncodeCheckUtil.get_charset(file1));
		List<String> fileContentsFile2 = FileUtils.readLines(file2,FileEncodeCheckUtil.get_charset(file2));
		if (fileContentsFile1.size() != fileContentsFile2.size()) {
			// log.error("发现不一致：【大小不同】"+targetFile+","+sourceFile);
			return false;
		}

		for (int i = 0; i < fileContentsFile1.size(); i++) {
			// log.info("进行对比:"+fileContents_mavenFile.get(i).trim()+"\n"+fileContents_productFile.get(i).trim());
			if (!StringUtils.equals(fileContentsFile1.get(i).trim(), fileContentsFile2.get(i).trim())) {
//				log.error("\n\n\n发现不一致【" + count++ + "】：【内容不同】\n" + file2 + "\n " + fileContentsFile1.get(i) + "\n" + file1 + "\n "
//						+ fileContentsFile2.get(i));
				return false;

			}
		}
		return true;// 通过
	}

	private static boolean ignore(String content_mavenFile, String content_prodFile) {
		// if (content_mavenFile.contains("page import")
		// || content_mavenFile.contains("sessionInfoKey")
		// || (content_mavenFile.contains("UniConstant") &&
		// content_prodFile.contains("Constant"))
		// || content_prodFile.contains(";\"%>"))
		// {//import="com.sitech.echn.service.card.webchoosenos.model.SLstAvailableNoOut.AvailableNoInfo;"%>
		// return true;
		// }
		return false;
	}
}
