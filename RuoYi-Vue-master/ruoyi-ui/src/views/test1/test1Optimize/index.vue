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
      <el-form-item label="代码类型" prop="type">
        <el-input
          v-model="queryParams.type"
          placeholder="请输入代码类型"
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
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['test1:test1Optimize:edit']"
        >优化</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="codeListList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="代码名称" align="center" prop="name" />
      <el-table-column label="代码版本" align="center" prop="version" />
      <el-table-column label="代码类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.code_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['test1:test1Optimize:edit']"
          >优化</el-button>
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-delete"-->
<!--            @click="handleDelete(scope.row)"-->
<!--            v-hasPermi="['test1:test1Optimize:remove']"-->
<!--          >删除</el-button>-->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="openFile(scope.row.name+' v'+scope.row.version)"
          >查看优化结果
          </el-button>
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

    <!-- 优化测试用例集对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="代码名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入代码名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="测试用例">
          <el-tag
            v-for="testCase in testCases"
            :key="testCase.id"
            closable
            :disable-transitions="false"
          >
            {{ testCase.name }}
          </el-tag>
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
import { listTest1Optimize, getTest1Optimize, delTest1Optimize, addTest1Optimize, updateTest1Optimize } from "@/api/test1/test1Optimize";
import { listCodeList } from "@/api/code/codeList";
import { getByCodeName } from "@/api/test1/test1List"; // 引入获取测试用例的方法

export default {
  name: "Test1Optimize",
  dicts: ['code_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
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
      // 测试用例集表格数据
      test1OptimizeList: [],
      // 代码列表表格数据
      codeListList: [],
      // 存储测试用例的数组
      testCases: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null
      },
      // 表单参数
      form: {
        id: null,
        name: null,
        path: null,
        status: null,
        userId: null,
        time: null,
        remark: null,
        type: null,
        testCaseNames: [] // 添加测试用例名字数组
      },
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    openFile(fileName){
      window.open(process.env.VUE_APP_BASE_API+'/result/'+fileName+"优化结果.txt", '_blank');
    },
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
        type: null,
        testCaseNames: [] // 重置测试用例名字数组
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
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加测试用例集";
    },
    /** 优化按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getTest1Optimize(id).then(response => {
        this.form = response.data;
        // 获取对应代码的测试用例
        getByCodeName(this.form.name).then(testResponse => {
          this.testCases = testResponse.data;
          // 将测试用例名字存入表单参数
          this.form.testCaseNames = this.testCases.map(testCase => testCase.name);
          this.open = true;
          this.title = "测试用例集优化";
        });
      });
    },
    /** 优化确定按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTest1Optimize(this.form).then(response => {
              this.$modal.msgSuccess("优化成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTest1Optimize(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除测试用例集优化编号为"' + ids + '"的数据项？').then(function() {
        return delTest1Optimize(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('test1/test1Optimize/export', {
        ...this.queryParams
      }, `test1Optimize_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
