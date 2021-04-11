package com.qizhuo.framework.base.Character;

import android.text.TextUtils;


import com.qizhuo.framework.gamedata.dao.entity.GameEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalGroupSearch {

    /**
     * 按群号-群名拼音搜索
     *
     * @param str
     */
    public static ArrayList<GameEntity> searchGroup(String str, ArrayList<GameEntity> allContacts) {
        ArrayList<GameEntity> groupList = new ArrayList<GameEntity>();
        // 如果搜索条件以0 1 +开头则按号码搜索
//        if (str.toString().startsWith("0") || str.toString().startsWith("1")
//                || str.toString().startsWith("+")) {
//            for (GameEntity group : allContacts) {
//                if (getGroupName(group) != null
//                        && group.getName().toString() + "" != null) {
//                    if ((group.getName().toString()+ "").contains(str)
//                            || group.getName().toString().contains(str)) {
//                        groupList.add(group);
//                    }
//                }
//            }
//            return groupList;
//        }
        CharacterParser finder = CharacterParser.getInstance();
        String result = "";
        for (GameEntity group : allContacts) {
            // 先将输入的字符串转换为拼音
            finder.setResource(str.toString());
            result = finder.getSpelling();
            if (contains(group, result)) {
                groupList.add(group);
            } else if ((group.getName() + "").contains(str)) {
                groupList.add(group);
            }
        }
        return groupList;
    }

//    /**
//     * 根据拼音搜索
//     *
//     * @param str
//     *            正则表达式
//     * @param pyName
//     *            拼音
//     * @param isIncludsive
//     *            搜索条件是否大于6个字符
//     * @return
//     */
    private static boolean contains(GameEntity group, String search) {
        if (TextUtils.isEmpty(group.getName().toString()) && TextUtils.isEmpty(group.  getName().toString()) ){
            return false;
        }
        boolean flag = false;
        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            String firstLetters = FirstLetterUtil.getFirstLetter(getGroupName(group));
            // 不区分大小写
            Pattern firstLetterMatcher = Pattern.compile(search,
                    Pattern.CASE_INSENSITIVE);
            flag = firstLetterMatcher.matcher(firstLetters).find();
        }

        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
            // 全拼匹配
            CharacterParser finder = CharacterParser.getInstance();
            finder.setResource(getGroupName(group));
            // 不区分大小写
            Pattern pattern2 = Pattern
                    .compile(search, Pattern.CASE_INSENSITIVE);
            Matcher matcher2 = pattern2.matcher(finder.getSpelling());
            flag = matcher2.find();
        }

        return flag;
    }

    private static String getGroupName(GameEntity group) {
        String strName = null;
        if (!TextUtils.isEmpty(group.getName().toString())) {
            strName = group.getName().toString();
        } else if (!TextUtils.isEmpty(group.getName().toString())) {
            strName =group.getName().toString();
        } else {
            strName = "";
        }
        return strName;
    }
}