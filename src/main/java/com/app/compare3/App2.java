package com.app.compare3;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private List<String> list3 = new ArrayList<String>();// devold 0  devnew 1

	public static void main(String[] args) throws Exception {
		
		//DEV分支的拆工程前后分析处理
//		dirStrOld = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
//		dirStrBase = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/base-module/src/main/";
//		dirStrCuse = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/cust-module/src/main/";
//		dirStrPay = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/pay-module/src/main/";
		//RM分支的拆工程前后分析处理
		dirStrOld = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";
		dirStrBase = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/base-module/src/main/";
		dirStrCuse = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/cust-module/src/main/";
		dirStrPay = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/pay-module/src/main/";
		
		App2 app2 = new App2(new FileCompareStrategyByFileContent(), dirStrOld, dirStrBase, dirStrCuse, dirStrPay);
		app2.deal();
	}

	private void deal() throws Exception {
		log.info("初始化完成,开始处理");
		iniFileListInDirs(fileListOld, dirStrOld);
		iniFileListInDirs(fileListNew, dirStrBase, dirStrCuse, dirStrPay);
		log.info("devFileListOld数为：" + fileListOld.size() + ", devFileListNew数为：" + fileListNew.size());

		dealExistFileList(fileListOld, new String[]{dirStrOld}, new String[]{dirStrBase,dirStrCuse,dirStrPay}, list1);
		dealExistFileList(fileListNew, new String[]{dirStrBase,dirStrCuse,dirStrPay}, new String[]{dirStrOld}, list3);

		dealEqualsFileList(fileListOld, new String[]{dirStrOld}, new String[]{dirStrBase,dirStrCuse,dirStrPay}, set2, set4Code);
		dealEqualsFileList(fileListNew, new String[]{dirStrBase,dirStrCuse,dirStrPay}, new String[]{dirStrOld}, set2, set4Code);

		log.info("处理完毕，结果如下：");
		printResult("new-notexsit,", list1, null);
		printResult("old_new_notequal,", set2, null);
		printResult("old-notexsit,", list3, null);
		printResult("encode,", set4Code, null);
		log.info("输出完毕！");
	}

}
