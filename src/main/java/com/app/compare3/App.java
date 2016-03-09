package com.app.compare3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.app.compare.FileCompareStrategy;
import com.app.compare.FileCompareStrategyByFileContent;

import lombok.extern.log4j.Log4j;

/**
 * <pre>
 * 移动总部二期：
 * 对比DEV分支和RM分支的代码差异，对比结果：
 * 文件/目录  是否差异		差异值
 * XXX.JAVA  有               DEV有，RM无
 * XXX.JAVA  有               DEV无，RM有
 * XXX.JAVA  有               DEV和RM内容不一致
 * 
 * 有的可能已经处理过了，这时可以在APP.CONF增加白名单
 * </pre>
 * 
 * @author wangtlc
 * @date 2016年3月7日 下午2:30:50
 *
 *       修改日期 修改人 修改目的
 *
 */
@Log4j
public class App {
	static FileCompareStrategy fileCompareStrategy = new FileCompareStrategyByFileContent();
	static String devDirStr = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
	static String rmDirStr = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";
	static File devDir = new File(devDirStr);
	static File rmDir = new File(rmDirStr);

	// 分析结果
	static List<String> list1 = new ArrayList<String>();// dev1rm0
	static List<String> list2 = new ArrayList<String>();// devrm_no
	static List<String> list3 = new ArrayList<String>();// dev0rm1

	// 处理结果：白名单，即保留处理
	static List<String> list1Blank = new ArrayList<String>();// dev1rm0
	static List<String> list2Blank = new ArrayList<String>();// devrm_no
	static List<String> list3Blank = new ArrayList<String>();// dev0rm1

	public static void main(String[] args) throws Exception {
		initBlank();
		log.info("初始化完成！");
		log.info("开始处理");
		deal();
		log.info("处理完毕，结果如下：");
		printResult();
	}

	private static void deal() throws Exception {
		workInDir1(devDir);// 解决场景：1、DEV有/RM无，2、DEV和RM不一致
		workInDir2(rmDir);// 解决场景：RM有，DEV无
	}

	private static void printResult() {
		for (String tmp : list1) {
			String pre=pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, "");
			if (list1Blank.contains(pre)) {
				continue;
			}
			log.info("rm-notexsit," +pre);
		}
		for (String tmp : list2) {
			String pre=pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, "");
			if (list2Blank.contains(pre)) {
				continue;
			}
			log.info("dev_rm_notequal," +pre);
		}
		for (String tmp : list3) {
			String pre=pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, "");
			if (list3Blank.contains(pre)) {
				continue;
			}
			log.info("dev-notexsit," +pre);
		}
	}

	private static void initBlank() throws Exception {
		List<String> lines = IOUtils.readLines(App.class.getResourceAsStream("app.conf"), "UTF-8");
		String lockOwner="";
		for (String line : lines) {
			if (line.startsWith("#")) {//注释
				if (line.contains("rm-notexsit-blank")) {
					lockOwner="rm-notexsit-blank";
				}else if (line.contains("dev_rm_notequal-blank")) {
					lockOwner="dev_rm_notequal-blank";
				}else if (line.contains("dev-notexsit-blank")) {
					lockOwner="dev-notexsit-blank";
				}
			}else if (StringUtils.isNotBlank(line)) {
				if (lockOwner.equals("rm-notexsit-blank")) {
					list1Blank.add(line);
				}else if (lockOwner.equals("dev_rm_notequal-blank")) {
					list2Blank.add(line);
				}else if (lockOwner.equals("dev-notexsit-blank")) {
					list3Blank.add(line);
				}else {
					log.warn("注意，出现未知错误！！");
				}
			}
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
					list3.add(""+ rmFileTem);
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
					list1.add(""+ devFile);
				} else if (fileCompareStrategy.isEqualsTwoFile(rmFile, devFile)) {// 如果在本地存在且相等则不处理，否则拷贝
				} else {
					list2.add(""+ devFile);
				}
			}
		}
	}

	private static String pathDeal(String absolutePath) {
		return absolutePath.replace("\\", "/");
	}

}
