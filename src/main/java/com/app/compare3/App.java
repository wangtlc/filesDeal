package com.app.compare3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class App extends BaseApp {

	public App(FileCompareStrategy fileCompareStrategy, String... dirs) {
		super(fileCompareStrategy, dirs);
	}

	private static String devDirStr = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
	private static String rmDirStr = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";

	// 待处理的文件列表
	private List<File> devFileList = new ArrayList<File>();
	private List<File> rmFileList = new ArrayList<File>();

	// 分析结果
	private List<String> list1 = new ArrayList<String>();// dev1rm0
	private Set<String> set2 = new HashSet<String>();// devrm_no
	private Set<String> set4Code = new HashSet<String>();// ecode
	private List<String> list3 = new ArrayList<String>();// dev0rm1

	// 处理结果：白名单，即保留处理
	private List<String> list1Blank = new ArrayList<String>();// dev1rm0
	private List<String> list2Blank = new ArrayList<String>();// devrm_no
	private List<String> list3Blank = new ArrayList<String>();// dev0rm1

	public static void main(String[] args) throws Exception {
		App app = new App(new FileCompareStrategyByFileContent(), devDirStr, rmDirStr);
		app.deal();
	}

	private void deal() throws Exception {
		initBlank();

		log.info("初始化完成,开始处理");
		iniFileListInDirs(devFileList, devDirStr);
		iniFileListInDirs(rmFileList, rmDirStr);
		log.info("devFileList数为：" + devFileList.size() + ", rmFileList数为：" + rmFileList.size());

		dealExistFileList(devFileList, new String[] { devDirStr }, new String[] { rmDirStr }, list1);
		dealExistFileList(rmFileList, new String[] { rmDirStr }, new String[] { devDirStr }, list3);

		dealEqualsFileList(devFileList, new String[] { devDirStr }, new String[] { rmDirStr }, set2, set4Code);
		dealEqualsFileList(rmFileList, new String[] { rmDirStr }, new String[] { devDirStr }, set2, set4Code);

		log.info("处理完毕，结果如下：");
		printResult("rm-notexsit,", list1, list1Blank);
		printResult("dev_rm_notequal,", set2, list2Blank);
		printResult("dev-notexsit,", list3, list3Blank);
		printResult("encode,", set4Code, null);
		log.info("输出完毕！");
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
}
