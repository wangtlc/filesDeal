package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.bean.SimpleValueByRef;

/**
 * 乱码检查 
 * modify by wangtlc 20160307
 * 
 * @Package: com.util 
 * @ClassName: FileBadEncodeCheckUtil
 * @author 王陶林 wangtlc@si-tech.com.cn
 * @date 2013-4-26 上午8:08:41
 * @Copyright © SI-TECH 2013. All rights reserved
 * @version: V1.0
 * 
 *           修改日期 修改人 修改目的
 * 
 */
public class FileBadEncodeCheckUtil {
	private static String letter = "<@>$|=+①②③④☆^⑴⑵⑶⑷⑸~↓×★◆ ";
	public static List<Character> resultList=new ArrayList<Character>();

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}


	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!isLetterOrDigit(c) && !isChinese(c)) {
				count = count + 1;
				if (!resultList.contains(c)) {
					resultList.add(c);
				}
				// System.out.print(c);
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}

	}

	private static boolean isLetterOrDigit(char c) {
		if ((int)c==65279) {
			return true;//这是个空串
		}
		if (letter.indexOf(c) != -1) {
			return true;
		}
		return Character.isLetterOrDigit(c);
	}

	public static void main(String[] args) {
		// checkHasBadEncode(new File(""));
		System.out.println(isMessyCode("*��JTP.jar�ļ����JTP�ļ���ȡ��ͼƬ��Դ"));
		System.out.println(isMessyCode("<@>你好"));
	}

	/**
	 * 乱码
	 * @author 王陶林 wangtlc@si-tech.com.cn
	 * @date 2013-4-26 上午9:28:56
	 *
	 * @param file
	 * @return
	 */
	public static boolean checkHasBadEncode(File file) {
		// 逐个处理JSP文件
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), FileEncodeCheckUtil.get_charset(file)));
			String lineContent = null;
			while ((lineContent = br.readLine()) != null) {
				if (isMessyCode(lineContent)) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	/**
	 *	文件本身编码和指定的编码不一致
	 *<%@ page contentType="text/html; charset=GBK"%>
	 *<%@ page language="java" pageEncoding="GBK"%>
	 * @author 王陶林 wangtlc@si-tech.com.cn
	 * @date 2013-4-26 上午9:29:49
	 *
	 * @param file
	 * @param simpleValueByRef 
	 * @return
	 */
	public static boolean selfEncodeNotEqualsPageEncoding(File file, SimpleValueByRef simpleValueByRef) {
		// 逐个处理JSP文件
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), FileEncodeCheckUtil.get_charset(file)));
			String lineContent = null;
			while ((lineContent = br.readLine()) != null) {
				if (lineContent.toLowerCase().indexOf("page")!=-1 && 
						(lineContent.toLowerCase().indexOf("charset")!=-1 || lineContent.toLowerCase().indexOf("pageencoding")!=-1) &&
						lineContent.toLowerCase().indexOf(FileEncodeCheckUtil.get_charset(file).toLowerCase())==-1) {
					simpleValueByRef.object=lineContent;
					return true;
				}
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;

	}
}
