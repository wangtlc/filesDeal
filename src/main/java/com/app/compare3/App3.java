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
 * 对比rm/RM分支，原整工程和拆分后的三个工程代码是否一致，对比结果：
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
public class App3 extends BaseApp {
	public App3(FileCompareStrategy fileCompareStrategy, String... dirs) {
		super(fileCompareStrategy, dirs);
	}

	
	private static String rmDirStrOld = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt/src/main/";
	private static String rmDirStrBase = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/base-module/src/main/";
	private static String rmDirStrCuse = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/cust-module/src/main/";
	private static String rmDirStrPay = "E:/workspace-stq-all/chinamobile-jt/echd-chinamobile-jt-prod/web-jt-parent/pay-module/src/main/";

	private List<File> rmFileListOld = new ArrayList<File>();
	private List<File> rmFileListNew = new ArrayList<File>();

	// 分析结果
	private List<String> list1 = new ArrayList<String>();// rm1rm0
	private Set<String> set2 = new HashSet<String>();// rmrm_no
	private Set<String> set4Code = new HashSet<String>();// ecode
	private List<String> list3 = new ArrayList<String>();// rmold 0  rmnew 1

	public static void main(String[] args) throws Exception {
		App3 app3 = new App3(new FileCompareStrategyByFileContent(), rmDirStrOld, rmDirStrBase, rmDirStrCuse, rmDirStrPay);
		app3.deal();
	}

	private void deal() throws Exception {
		log.info("初始化完成,开始处理");
		iniFileListInDirs(rmFileListOld, rmDirStrOld);
		iniFileListInDirs(rmFileListNew, rmDirStrBase, rmDirStrCuse, rmDirStrPay);
		log.info("rmFileListOld数为：" + rmFileListOld.size() + ", rmFileListNew数为：" + rmFileListNew.size());

		dealExistFileList(rmFileListOld, new String[]{rmDirStrOld}, new String[]{rmDirStrBase,rmDirStrCuse,rmDirStrPay}, list1);
		dealExistFileList(rmFileListNew, new String[]{rmDirStrBase,rmDirStrCuse,rmDirStrPay}, new String[]{rmDirStrOld}, list3);

		dealEqualsFileList(rmFileListOld, new String[]{rmDirStrOld}, new String[]{rmDirStrBase,rmDirStrCuse,rmDirStrPay}, set2, set4Code);
		dealEqualsFileList(rmFileListNew, new String[]{rmDirStrBase,rmDirStrCuse,rmDirStrPay}, new String[]{rmDirStrOld}, set2, set4Code);

		// dealrmFileList();// 解决场景：1、rm有/RM无，2、rm和RM不一致
		// dealRmFileList();// 解决场景：RM有，rm无

		log.info("处理完毕，结果如下：");
		printResult("new-notexsit,", list1, null);
		printResult("old_new_notequal,", set2, null);
		printResult("old-notexsit,", list3, null);
		printResult("encode,", set4Code, null);
		log.info("输出完毕！");
	}

}
