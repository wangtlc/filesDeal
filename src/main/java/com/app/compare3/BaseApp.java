package com.app.compare3;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.app.compare.FileCompareStrategy;
import com.app.compare.FileCompareStrategyByFileContent;
import com.util.FileEncodeCheckUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class BaseApp {
	public BaseApp(FileCompareStrategy fileCompareStrategy, String... dirs) {
		super();
		this.fileCompareStrategy = fileCompareStrategy;
		this.dirs = dirs;
	}

	private FileCompareStrategy fileCompareStrategy = new FileCompareStrategyByFileContent();
	private String[] dirs;

	private String removePre(String tmp) {
		String result = tmp;
		for (String dir : dirs) {
			result = pathDeal(result).replaceAll(dir, "");
		}
		return result;// 将所有前缀去除;
	}

	protected void iniFileListInDirs(List<File> fileList, String... dirs) throws Exception {
		for (String dir : dirs) {
			iniFileListInDir(fileList, new File(dir));
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

	/**
	 * <pre>
	 * 原文件：遍历sourceFileList里面所有文件 
	 * 目标文件：将原前缀sourcePre替换为targetPre 
	 * 查看两个文件差异。
	 * </pre>
	 */
	protected void dealEqualsFileList(List<File> sourceFileList, String[] sourcePre, String[] targetPre, Collection<String> result,
			Collection<String> result4code) throws Exception {
		for (File sourceFile : sourceFileList) {
			if (sourceFile.isDirectory()) {
				continue;
			}
			// 原目录可能有多种，所以要逐一考虑
			for (int i = 0; i < sourcePre.length; i++) {
				// 目标文件可能分布在多个目录里，所以要逐一试探
				for (int j = 0; j < targetPre.length; j++) {
					File targetFile = new File(targetPre[j] + pathDeal(sourceFile.getAbsolutePath()).replace(sourcePre[i], ""));
					if (sourceFile.exists() && targetFile.exists()) {
						// 分析文件是否一致
						if (!fileCompareStrategy.isEqualsTwoFile(sourceFile, targetFile)) {
							result.add(removePre("" + sourceFile));
						}
						// 分析编码问题
						if (!FileEncodeCheckUtil.get_charset(sourceFile).equals(FileEncodeCheckUtil.get_charset(targetFile))) {
							result4code.add(removePre("" + sourceFile));
						}
					}
				}
			}
		}
	}

	/**
	 * <pre>
	 * 原文件：遍历sourceFileList里面所有文件 
	 * 目标文件：将原前缀sourcePre替换为targetPre 
	 * 查看目标文件是否存在。
	 * </pre>
	 */
	protected void dealExistFileList(List<File> sourceFileList, String[] sourcePre, String[] targetPre, List<String> result) throws Exception {
		for (File sourceFile : sourceFileList) {
			if (sourceFile.isDirectory()) {
				continue;
			}
			if (!doDealExistFile(sourceFile, sourcePre, targetPre)) {
				result.add(removePre("" + sourceFile));
			}
		}
	}

	private boolean doDealExistFile(File sourceFile, String[] sourcePre, String[] targetPre) {
		// 原目录可能有多种，所以要逐一考虑
		for (int i = 0; i < sourcePre.length; i++) {
			// 目标文件可能分布在多个目录里，所以要逐一试探
			for (int j = 0; j < targetPre.length; j++) {
				File targetFile = new File(targetPre[j] + pathDeal(sourceFile.getAbsolutePath()).replace(sourcePre[i], ""));
				if (targetFile.exists()) {
					return true;// 如果找到则直接返回
				}
			}
		}
		return false;
	}

	protected void printResult(String preTmp, Collection<String> target, List<String> listBlank) {
		for (String tmp : target) {
			if (listBlank != null && listBlank.contains(tmp)) {
				continue;
			}
			log.info(preTmp + tmp);
		}
	}

	protected String pathDeal(String absolutePath) {
		return absolutePath.replace("\\", "/");
	}
}
