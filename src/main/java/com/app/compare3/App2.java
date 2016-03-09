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
public class App2 extends BaseApp {
	public App2(FileCompareStrategy fileCompareStrategy, String... dirs) {
		super(fileCompareStrategy, dirs);
	}

	private static String dirStrOld = "";
	private static String dirStrBase = "";
	private static String dirStrCuse = "";
	private static String dirStrPay = "";

	private List<File> fileListOld = new ArrayList<File>();
	private List<File> fileListNew = new ArrayList<File>();

	// 分析结果
	private List<String> list1 = new ArrayList<String>();// dev1rm0
	private Set<String> set2 = new HashSet<String>();// devrm_no
	private Set<String> set4Code = new HashSet<String>();// ecode
	private List<String> list3 = new ArrayList<String>();// devold 0 devnew 1

	// 处理结果：白名单，即保留处理
	private List<String> list1Blank = new ArrayList<String>();// dev1rm0
	private List<String> list2Blank = new ArrayList<String>();// devrm_no
	private List<String> list3Blank = new ArrayList<String>();// dev0rm1

	public static void main(String[] args) throws Exception {

		initDev();// DEV分支的拆工程前后分析处理
		// initRM();// RM分支的拆工程前后分析处理

		App2 app2 = new App2(new FileCompareStrategyByFileContent(), dirStrOld, dirStrBase, dirStrCuse, dirStrPay);
		app2.deal();
	}

	private static void initRM() {
		dirStrOld = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";
		dirStrBase = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/base-module/src/main/";
		dirStrCuse = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/cust-module/src/main/";
		dirStrPay = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/pay-module/src/main/";
	}

	private static void initDev() {
		dirStrOld = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
		dirStrBase = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/base-module/src/main/";
		dirStrCuse = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/cust-module/src/main/";
		dirStrPay = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/pay-module/src/main/";
	}

	private void deal() throws Exception {
		initBlank();
		log.info("初始化完成,开始处理");
		iniFileListInDirs(fileListOld, dirStrOld);
		iniFileListInDirs(fileListNew, dirStrBase, dirStrCuse, dirStrPay);
		log.info("devFileListOld数为：" + fileListOld.size() + ", devFileListNew数为：" + fileListNew.size());

		dealExistFileList(fileListOld, new String[] { dirStrOld }, new String[] { dirStrBase, dirStrCuse, dirStrPay }, list1);
		dealExistFileList(fileListNew, new String[] { dirStrBase, dirStrCuse, dirStrPay }, new String[] { dirStrOld }, list3);

		dealEqualsFileList(fileListOld, new String[] { dirStrOld }, new String[] { dirStrBase, dirStrCuse, dirStrPay }, set2, set4Code);
		dealEqualsFileList(fileListNew, new String[] { dirStrBase, dirStrCuse, dirStrPay }, new String[] { dirStrOld }, set2, set4Code);

		log.info("处理完毕，结果如下：");
		printResult("new-notexsit,", list1, list1Blank);
		printResult("old_new_notequal,", set2, list2Blank);
		printResult("old-notexsit,", list3, list3Blank);
		printResult("encode,", set4Code, null);
		log.info("输出完毕！");
	}

	// 初始化白名单
	private void initBlank() throws Exception {
		List<String> lines = IOUtils.readLines(App.class.getResourceAsStream("app2.conf"), "UTF-8");
		String lockOwner = "";
		for (String line : lines) {
			if (line.startsWith("#")) {// 注释
				if (line.contains("new-notexsit-blank")) {
					lockOwner = "new-notexsit-blank";
				} else if (line.contains("old_new_notequal-blank")) {
					lockOwner = "old_new_notequal-blank";
				} else if (line.contains("old-notexsit-blank")) {
					lockOwner = "old-notexsit-blank";
				}
			} else if (StringUtils.isNotBlank(line)) {
				if (lockOwner.equals("new-notexsit-blank")) {
					list1Blank.add(line);
				} else if (lockOwner.equals("old_new_notequal-blank")) {
					list2Blank.add(line);
				} else if (lockOwner.equals("old-notexsit-blank")) {
					list3Blank.add(line);
				} else {
					log.warn("注意，出现未知错误！！");
				}
			}
		}
	}
}
