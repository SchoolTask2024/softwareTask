<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="代码名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入代码名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="codeListList">
      <el-table-column label="代码名称" align="center" prop="name" />
      <el-table-column label="代码版本" align="center" prop="version" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleAnalysis(scope.row)"
          >查看数据</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <label>运行：</label>
      <select v-if="open" v-model="dataId"  @change="handleSetBarChartData" >
        <option v-for="option in chartData" :key="option.id" :value="option.id">{{ option.resultName + " "+ option.time}}</option>
      </select>
      <my-bar-chart v-if="open" :chart-data="barChartData"/>
      <my-radar-chart  v-if="open" :chart-data="radarChartData"/>
    </el-dialog>
  </div>
</template>

<script>
import {listCodeList} from "@/api/code/codeList";
import PanelGroup from "@/views/dashboard/PanelGroup.vue";
import LineChart from "@/views/dashboard/LineChart.vue";
import MyBarChart from "@/components/MyBarChart/index.vue";
import {analysis} from "@/api/codeRunning/result";
import MyRadarChart from "@/components/MyRadarChart/index.vue";

export default {
  name: "analysis",
  components: {MyRadarChart, MyBarChart, LineChart, PanelGroup},
  data() {
    return {
      // 遮罩层
      loading: true,
      fileType:['java'],
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 代码列表表格数据
      codeListList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      isAdd: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null
      },
      dataId:null,
      chartData:null,
      barChartData:null,
      radarChartData:null,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },

  created() {
    this.getList();
  },

  methods: {
    /** 查询代码列表列表 */
    getList() {
      this.loading = true;
      listCodeList(this.queryParams).then(response => {
        this.codeListList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        path: null,
        status: null,
        userId: null,
        time: null,
        remark: null,
        type: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleAnalysis(code){
      this.chartData=null;
      this.barChartData = null;

      analysis(code.id).then(response=>{
        this.chartData=response.data;

        if(this.chartData.length === 0){
          this.$message({
            message: "没有数据",
            type: "warning"
          });
        }else {
          this.title = code.name + "的数据"
          this.dataId = this.chartData[0].id;
          this.barChartData = {
            id:this.chartData[0].id,
            conditions :JSON.parse(this.chartData[0].conditions),
            coverageData:JSON.parse(this.chartData[0].coverageData)
          }
          // console.log(this.chartData);
          this.open = true;
          // this.radarChartData = {
          //   id:this.chartData[0].id,
          //   data:this.chartData[0].data,
          //   indicatorData:this.chartData[0].indicatorData,
          //   legendData:this.chartData[0].legendData
          //
          // }
         this.radarChartData = {
           legendData:['文件1', '文件2', '文件3'],
            indicatorData:[
              { name: '分支1', max: 1 },
              { name: '分支2', max: 1 },
              { name: '分支3', max: 1 },
              { name: '分支4', max: 1 },
              { name: '分支5', max: 1 },
              { name: '分支6', max: 1 }
            ],
            data:[
              {
                value: [0, 0.3, 0.6, 1, 1, 1],
                name: '文件1'
              },
              {
                value: [1, 0.1, 0.6],
                name: '文件2'
              },
              {
                value: [0, 1, 1,0.8,1,1,0.3],
                name: '文件3'
              }
            ],
          }

        }

      });
    },
    handleSetBarChartData(){
      let data = this.chartData.find(option => option.id === this.dataId);
      this.barChartData = {
        id:data.id,
        conditions :JSON.parse(data.conditions),
        coverageData:JSON.parse(data.coverageData)
      }
      // this.radarChartData = {
      //   id:data.id,
      //   data:data.data,
      //   indicatorData:data.indicatorData,
      //   legendData:data.legendData
      // }

    }
  }

};
</script>
