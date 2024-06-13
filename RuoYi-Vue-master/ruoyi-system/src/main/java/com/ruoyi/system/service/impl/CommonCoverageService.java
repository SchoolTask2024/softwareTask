package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.CoverageData;
import com.ruoyi.system.service.ICommonCoverageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
@Service
public class CommonCoverageService implements ICommonCoverageService {
    @Value("${result.path}")
    private String resultPath;
    @Value("${result.type}")
    private String fileType;

    /**
     * 获取报告文件文件夹
     * @return 地址
     */
    @Override
    public String getResultPath() {
        return resultPath;
    }

    /**
     * 生成报告文件类型
     * @return 类型
     */
    @Override
    public String getFileType() {
        return fileType;
    }
    private Integer compare(ArrayList<Integer> a, ArrayList<Integer> b){
        if(a.size() != b.size()){
            return -1;
        }
        boolean flag = false;
        Integer index = -1;
        for(int i = 0; i < a.size(); i++){
            if(a.get(i) != b.get(i)){
                if(!flag){
                    flag = true;
                    index = i;
                    continue;
                }
                if(flag){
                    return -1;
                }
            }
        }
        return index;
    }

    /**
     * 计算覆盖率
     * @param cData 数据
     * @return 结果
     */
    @Override
    public String calculateCoverage(ArrayList<CoverageData> cData){
        ArrayList<ArrayList<Integer>> trueList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> falseList = new ArrayList<>();

        for (CoverageData data : cData) {
            ArrayList<Integer> paramList = parseParam(data.getParam());
            if (data.getResult()) {
                if (!containsList(trueList, paramList)) {
                    trueList.add(paramList);
                }
            } else {
                if (!containsList(falseList, paramList)) {
                    falseList.add(paramList);
                }
            }
        }

        int testsize;
        if(trueList.size()!=0){
            testsize=trueList.get(0).size();
        }else {
            testsize=falseList.get(0).size();
        }

        int[] result = new int[testsize]; // 用于记录结果的数组


        for (int i = 0; i < trueList.size(); i++) {
            ArrayList<Integer> row0 = trueList.get(i);
            for(int j =0 ;j<falseList.size();j++){
                ArrayList<Integer> row1 = falseList.get(j);
                if(compare(row0,row1)!=-1){
                    result[compare(row0,row1)]=1;
                }
//                System.out.println(i);
            }

        }

        double great=0;
        double all=result.length;
        // 打印结果数组
        System.out.println("Comparison result:");
        for (int i = 0; i < result.length; i++) {
            if(result[i]==1){
                great++;
            }
//            System.out.println("array[" + i + "] = " + result[i]);
        }

        return String.valueOf(great/all);
    }

    /**
     * 获取进程的输出
     * @param process 进程
     * @return 输出
     * @throws IOException
     */
    @Override
    public String getProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
    // 检查列表中是否包含某个 ArrayList<Integer>
    private boolean containsList(ArrayList<ArrayList<Integer>> list, ArrayList<Integer> targetList) {
        for (ArrayList<Integer> innerList : list) {
            if (innerList.equals(targetList)) {
                return true;
            }
        }
        return false;
    }
    // 解析 param 字符串为 ArrayList<Integer>
    private ArrayList<Integer> parseParam(String param) {
        ArrayList<Integer> list = new ArrayList<>();
        String[] tokens = param.split("\\s+");
        for (String token : tokens) {
            list.add(Integer.parseInt(token));
        }
        return list;
    }

}
