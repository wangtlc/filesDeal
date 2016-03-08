package com.app.compare3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.app.compare.FileCompareStrategy;
import com.app.compare.FileCompareStrategyByFileContent;

import lombok.extern.log4j.Log4j;

/**
 * <pre>
 * 移动总部二期：
 * 对比DEV/RM分支，原整工程和拆分后的三个工程代码是否一致，对比结果：
 * 文件/目录  是否差异		差异值
 * XXX.JAVA  有               原有，拆无
 * XXX.JAVA  有               原无，拆有
 * XXX.JAVA  有               原和拆内容不一致
 * 
 * </pre>
 * 
 * @author wangtlc
 * @date 2016年3月7日 下午2:30:50
 *
 *       修改日期 修改人 修改目的
 *
 */
@Log4j
public class App2 {
	static FileCompareStrategy fileCompareStrategy = new FileCompareStrategyByFileContent();
	static String devDirStr = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
	static String rmDirStr = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";
	static File devDir = new File(devDirStr);
	static File rmDir = new File(rmDirStr);

	static List<String> list1 = new ArrayList<String>();// dev1rm0
	static List<String> list2 = new ArrayList<String>();// devrm_no
	static List<String> list3 = new ArrayList<String>();// dev0rm1

	public static void main(String[] args) throws Exception {
		log.info("开始处理");
		workInDir1(devDir);// 解决场景：1、DEV有/RM无，2、DEV和RM不一致
		workInDir2(rmDir);// 解决场景：RM有，DEV无
		log.info("处理完毕，结果如下：");
		for (String tmp : list1) {
			log.info(pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, ""));	
		}
		for (String tmp : list2) {
			log.info(pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, ""));	
		}
		for (String tmp : list3) {
			log.info(pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, ""));	
		}
	}

	public static void workInDir2(File rmFileP) throws Exception {
		if (rmFileP.getAbsolutePath().contains("webapp")) {
			log.warn("【跳过该文件：】" + rmFileP.getName());
			return;
		}
		File[] rmFiles = rmFileP.listFiles();
		for (File rmFileTem : rmFiles) {
			if (rmFileTem.isDirectory()) {
				workInDir2(rmFileTem);
			} else {
				File rmFile = new File(devDirStr + pathDeal(rmFileTem.getAbsolutePath()).replace(rmDirStr, ""));
				if (!rmFile.exists()) {
					list3.add("dev-notexsit," + rmFileTem);
				}
			}
		}
	}

	public static void workInDir1(File devFileP) throws Exception {
		if (devFileP.getAbsolutePath().contains("webapp")) {
			log.warn("【跳过该文件：】" + devFileP.getName());
			return;
		}
		File[] devFiles = devFileP.listFiles();
		for (File devFile : devFiles) {
			if (devFile.isDirectory()) {
				workInDir1(devFile);
			} else {
				File rmFile = new File(rmDirStr + pathDeal(devFile.getAbsolutePath()).replace(devDirStr, ""));
				if (!rmFile.exists()) {
					list1.add("rm-notexsit," + devFile);
				} else if (fileCompareStrategy.isEqualsTwoFile(rmFile, devFile)) {// 如果在本地存在且相等则不处理，否则拷贝
				} else {
					list2.add("dev_rm_notequal," + devFile);
				}
			}
		}
	}

	private static String pathDeal(String absolutePath) {
		return absolutePath.replace("\\", "/");
	}

}
