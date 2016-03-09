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

	private static String devDirStrOld = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
	private static String devDirStrBase = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/base-module/src/main/";
	private static String devDirStrCuse = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/cust-module/src/main/";
	private static String devDirStrPay = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/pay-module/src/main/";

	private List<File> devFileListOld = new ArrayList<File>();
	private List<File> devFileListNew = new ArrayList<File>();

	// 分析结果
	private List<String> list1 = new ArrayList<String>();// dev1rm0
	private Set<String> set2 = new HashSet<String>();// devrm_no
	private Set<String> set4Code = new HashSet<String>();// ecode
	private List<String> list3 = new ArrayList<String>();// devold 0  devnew 1

	public static void main(String[] args) throws Exception {
		App2 app2 = new App2(new FileCompareStrategyByFileContent(), devDirStrOld, devDirStrBase, devDirStrCuse, devDirStrPay);
		app2.deal();
	}

	private void deal() throws Exception {
		log.info("初始化完成,开始处理");
		iniFileListInDirs(devFileListOld, devDirStrOld);
		iniFileListInDirs(devFileListNew, devDirStrBase, devDirStrCuse, devDirStrPay);
		log.info("devFileListOld数为：" + devFileListOld.size() + ", devFileListNew数为：" + devFileListNew.size());

		dealExistFileList(devFileListOld, new String[]{devDirStrOld}, new String[]{devDirStrBase,devDirStrCuse,devDirStrPay}, list1);
		dealExistFileList(devFileListNew, new String[]{devDirStrBase,devDirStrCuse,devDirStrPay}, new String[]{devDirStrOld}, list3);

		dealEqualsFileList(devFileListOld, new String[]{devDirStrOld}, new String[]{devDirStrBase,devDirStrCuse,devDirStrPay}, set2, set4Code);
		dealEqualsFileList(devFileListNew, new String[]{devDirStrBase,devDirStrCuse,devDirStrPay}, new String[]{devDirStrOld}, set2, set4Code);

		// dealDevFileList();// 解决场景：1、DEV有/RM无，2、DEV和RM不一致
		// dealRmFileList();// 解决场景：RM有，DEV无

		log.info("处理完毕，结果如下：");
		printResult("new-notexsit,", list1, null);
		printResult("old_new_notequal,", set2, null);
		printResult("old-notexsit,", list3, null);
		printResult("encode,", set4Code, null);
	}

}
