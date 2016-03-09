package com.app.compare3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.app.compare.FileCompareStrategy;
import com.app.compare.FileCompareStrategyByFileContent;
import com.util.FileEncodeCheckUtil;

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
public class App extends BaseApp {
	FileCompareStrategy fileCompareStrategy = new FileCompareStrategyByFileContent();
	String devDirStr = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
	String rmDirStr = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";
	File devDir = new File(devDirStr);
	File rmDir = new File(rmDirStr);

	// 待处理的文件列表
	List<File> devFileList = new ArrayList<File>();
	List<File> rmFileList = new ArrayList<File>();

	// 分析结果
	List<String> list1 = new ArrayList<String>();// dev1rm0
	Set<String> set2 = new HashSet<String>();// devrm_no
	List<String> set3 = new ArrayList<String>();// ecode
	List<String> list3 = new ArrayList<String>();// dev0rm1

	// 处理结果：白名单，即保留处理
	List<String> list1Blank = new ArrayList<String>();// dev1rm0
	List<String> list2Blank = new ArrayList<String>();// devrm_no
	List<String> list3Blank = new ArrayList<String>();// dev0rm1

	public static void main(String[] args) throws Exception {
		App app = new App();
		app.deal();
	}

	private void deal() throws Exception {
		initBlank();

		log.info("初始化完成,开始处理");
		iniFileListInDirs(devFileList, devDirStr);
		iniFileListInDirs(rmFileList, rmDirStr);
		log.info("devFileList数为：" + devFileList.size() + ", rmFileList数为：" + rmFileList.size());

		dealExistFileList(devFileList,devDirStr, rmDirStr, list1);
		dealExistFileList(rmFileList, rmDirStr, devDirStr, list3);

		dealEqualsFileList(devFileList, devDirStr, rmDirStr, set2,set3);
		dealEqualsFileList(rmFileList, rmDirStr, devDirStr, set2,set3);

		// dealDevFileList();// 解决场景：1、DEV有/RM无，2、DEV和RM不一致
		// dealRmFileList();// 解决场景：RM有，DEV无

		log.info("处理完毕，结果如下：");
		printResult("rm-notexsit,", list1, list1Blank);
		printResult("dev_rm_notequal,", set2, list2Blank);
		printResult("dev-notexsit,", list3, list3Blank);
		printResult("encode,", set3, null);
	}

	/**
	 * 原文件：遍历sourceFileList里面所有文件 目标文件：将原前缀sourcePre替换为targetPre 查看两个文件差异。
	 */
	private void dealEqualsFileList(List<File> sourceFileList, String sourcePre, String targetPre, Collection<String> result, Collection<String> result4code) throws Exception {
		for (File sourceFile : sourceFileList) {
			if (sourceFile.isDirectory()) {
				continue;
			}
			File targetFile = new File(targetPre + pathDeal(sourceFile.getAbsolutePath()).replace(sourcePre, ""));
			if (sourceFile.exists() && targetFile.exists()) {
				//分析文件是否一致
				if (!fileCompareStrategy.isEqualsTwoFile(sourceFile, targetFile)) {
					if (!result.contains("" + sourceFile)) {
						result.add(removePre("" + sourceFile));
					}
				}
				//分析编码问题
				if (!FileEncodeCheckUtil.get_charset(sourceFile).equals(FileEncodeCheckUtil.get_charset(targetFile))) {
					result4code.add(removePre("" + sourceFile));
				}
			}
		}
	}

	/**
	 * 原文件：遍历sourceFileList里面所有文件 目标文件：将原前缀sourcePre替换为targetPre 查看目标文件是否存在。
	 */
	private void dealExistFileList(List<File> sourceFileList, String sourcePre, String targetPre, List<String> result) throws Exception {
		for (File sourceFile : sourceFileList) {
			if (sourceFile.isDirectory()) {
				continue;
			}
			File targetFile = new File(targetPre + pathDeal(sourceFile.getAbsolutePath()).replace(sourcePre, ""));
			if (!targetFile.exists()) {
				result.add(removePre("" + sourceFile));
			}
		}
	}

	private void printResult(String preTmp, Collection<String> target, List<String> listBlank) {
		for (String tmp : target) {
			if (listBlank!=null && listBlank.contains(tmp)) {
				continue;
			}
			log.info(preTmp + tmp);
		}
	}

	// 初始化白名单
	private void initBlank() throws Exception {
		List<String> lines = IOUtils.readLines(App.class.getResourceAsStream("app.conf"), "UTF-8");
		String lockOwner = "";
		for (String line : lines) {
			if (line.startsWith("#")) {// 注释
				if (line.contains("rm-notexsit-blank")) {
					lockOwner = "rm-notexsit-blank";
				} else if (line.contains("dev_rm_notequal-blank")) {
					lockOwner = "dev_rm_notequal-blank";
				} else if (line.contains("dev-notexsit-blank")) {
					lockOwner = "dev-notexsit-blank";
				}
			} else if (StringUtils.isNotBlank(line)) {
				if (lockOwner.equals("rm-notexsit-blank")) {
					list1Blank.add(line);
				} else if (lockOwner.equals("dev_rm_notequal-blank")) {
					list2Blank.add(line);
				} else if (lockOwner.equals("dev-notexsit-blank")) {
					list3Blank.add(line);
				} else {
					log.warn("注意，出现未知错误！！");
				}
			}
		}
	}

	private String removePre(String tmp) {
		return pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, "");//将所有前缀去除;
	}
}
