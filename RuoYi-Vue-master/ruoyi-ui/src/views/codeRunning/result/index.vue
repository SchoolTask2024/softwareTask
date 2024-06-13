<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="覆盖率" prop="coverageRate">
        <el-input
          v-model="queryParams.coverageRate"
          placeholder="请输入覆盖率"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="测试名称" prop="resultName">
        <el-input
          v-model="queryParams.resultName"
          placeholder="请输入测试名称"
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
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['codeRunning:result:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['codeRunning:result:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['codeRunning:result:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['codeRunning:result:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="resultList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="运行名称" align="center" prop="resultName" />
      <el-table-column label="代码" align="center">
        <template slot-scope="scope">
          {{ scope.row.codeName + ' v' + scope.row.codeVersion }}
        </template>
      </el-table-column>
      <el-table-column label="测试用例" align="center">
        <template slot-scope="scope">
          <el-tooltip
            class="item"
            effect="dark"
            :content="scope.row.test1List.map(test => test.name).join(', ')"
            placement="top"
          >
            <span>{{ scope.row.test1List.length }} 个用例</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="执行人" align="center" prop="userName" />
      <el-table-column label="运行时间" align="center" prop="time" />
      <el-table-column label="覆盖率" align="center" prop="coverageRate" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['codeRunning:result:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['codeRunning:result:remove']"
          >删除</el-button>
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

    <!-- 添加或修改代码运行对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="运行名称" prop="resultName">
          <el-input v-model="form.resultName" placeholder="请输入测试名称" />
        </el-form-item>
        <el-form-item label="运行代码" prop="codeId">
          <el-select v-model="form.codeId" placeholder="请选择代码">
            <el-option
              v-for="item in codeOptions"
              :key="item.id"
              :label="item.name + ' v'+item.version"
              :value="item.id"
              @click.native="selectCode(item.name)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用例" v-if="form.codeId!=null">
          <el-select v-model="form.testIds" placeholder="请选择测试用例" multiple>
            <el-option
              v-for="item in testOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="update" label="覆盖率" prop="coverageRate">
          <el-input v-model="form.coverageRate" placeholder="请输入覆盖率" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listResult, getResult, delResult, addResult, updateResult } from "@/api/codeRunning/result";
import {listCodeList} from "@/api/code/codeList";
import {getByCodeName} from "@/api/test1/test1List";

export default {
  name: "Result",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      codeOptions:[],
      testOptions:[],
      update: false,
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 代码运行表格数据
      resultList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        codeId: null,
        path: null,
        userId: null,
        time: null,
        coverageRate: null,
        resultName: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        resultName: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        codeId:[
          { required: true, message: "运行代码不能为空", trigger: "blur" },
        ]
      }
    };
  },
  created() {
    this.getCode();
    this.getList();
  },
  methods: {
    /** 查询代码运行列表 */
    getList() {
      this.loading = true;
      listResult(this.queryParams).then(response => {
        this.resultList = response.rows;
        // console.log(this.resultList)
        this.total = response.total;
        this.loading = false;
      });
    },
    getCode(){
      listCodeList().then(response =>{
        this.codeOptions = response.rows;
      })
    },
    selectCode(name){
      this.form.testIds = [];
      getByCodeName(name).then(response=>{
        this.testOptions= response.data;
      })
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
        codeId: null,
        path: null,
        userId: null,
        time: null,
        coverageRate: null,
        resultName: null,
        testIds:[]
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.update = false;
      this.title = "添加代码运行";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.update = true;
      const id = row.id || this.ids
      getResult(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改代码运行";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateResult(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            if (this.form.testIds.length<=0){
              return  this.$modal.msgError("请选择至少一个测试用例");
            }
            addResult(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除代码运行编号为"' + ids + '"的数据项？').then(function() {
        return delResult(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('codeRunning/result/export', {
        ...this.queryParams
      }, `result_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
