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
public class App2 extends BaseApp{
	FileCompareStrategy fileCompareStrategy = new FileCompareStrategyByFileContent();
	String devDirStrOld = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt/src/main/";
	String devDirStrBase = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/base-module/src/main/";
	String devDirStrCuse = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/cust-module/src/main/";
	String devDirStrPay = "E:/workspace-stq-all/chinamobile-jt/chinamobile-jt-dev/web-jt-parent/pay-module/src/main/";
	
	
	List<File> devFileListOld=new ArrayList<File>() ;
	List<File> devFileListNew =new ArrayList<File>() ;

	List<String> list1 = new ArrayList<String>();// dev1rm0
	List<String> list2 = new ArrayList<String>();// devrm_no
	List<String> list3 = new ArrayList<String>();// dev0rm1

	public static void main(String[] args) throws Exception {
		App2 app=new App2();
		log.info("开始初始化");
		app.deal();
	}
	
	private  void deal() throws Exception {
		iniFileListInDirs(devFileListOld,devDirStrOld);
		iniFileListInDirs(devFileListNew,devDirStrBase,devDirStrCuse,devDirStrPay);
		log.info("devFileListOld数为："+devFileListOld.size()+", devFileListNew数为："+ devFileListNew.size());
		for (File file : devFileListNew) {
			
		}
		
//		workInDir2(rmDir);// 解决场景：RM有，DEV无
//		log.info("处理完毕，结果如下：");
//		for (String tmp : list1) {
//			log.info(pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, ""));	
//		}
//		for (String tmp : list2) {
//			log.info(pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, ""));	
//		}
//		for (String tmp : list3) {
//			log.info(pathDeal(tmp).replaceAll(devDirStr, "").replaceAll(rmDirStr, ""));	
//		}
	}


//	private void workInDir1(File devFileP) throws Exception {
//		if (devFileP.getAbsolutePath().contains("webapp")) {
//			log.warn("【跳过该文件：】" + devFileP.getName());
//			return;
//		}
//		File[] devFiles = devFileP.listFiles();
//		for (File devFile : devFiles) {
//			if (devFile.isDirectory()) {
//				workInDir1(devFile);
//			} else {
//				File rmFile = new File(rmDirStr + pathDeal(devFile.getAbsolutePath()).replace(devDirStr, ""));
//				if (!rmFile.exists()) {
//					list1.add("rm-notexsit," + devFile);
//				} else if (fileCompareStrategy.isEqualsTwoFile(rmFile, devFile)) {// 如果在本地存在且相等则不处理，否则拷贝
//				} else {
//					list2.add("dev_rm_notequal," + devFile);
//				}
//			}
//		}
//	}

	private String pathDeal(String absolutePath) {
		return absolutePath.replace("\\", "/");
	}

}
