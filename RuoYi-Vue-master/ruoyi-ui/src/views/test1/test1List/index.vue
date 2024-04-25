<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="测试名称" prop="name">
        <el-input
          v-model="queryParams.name"
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
          v-hasPermi="['test1:test1List:add']"
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
          v-hasPermi="['test1:test1List:edit']"
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
          v-hasPermi="['test1:test1List:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['test1:test1List:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="test1ListList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="测试id" align="center" prop="id" />
      <el-table-column label="测试名称" align="center" prop="name" />
      <el-table-column label="所属代码" align="center" prop="codeName" />
      <el-table-column label="导入人员" align="center" prop="importUser" />
      <el-table-column label="导入时间" align="center" prop="time" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['test1:test1List:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['test1:test1List:remove']"
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

    <!-- 添加或修改测试列表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="测试名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入测试名称" />
        </el-form-item>
        <el-form-item label="所属代码" prop="codeName">
            <el-select v-model="form.codeName" placeholder="请选择代码">
              <el-option
                v-for="item in codeOptions"
                :key="item.name"
                :label="item.name"
                :value="item.name"
              />
            </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="测试路径" prop="path">
          <my-upload
            v-model="form.path"
            path="/test1/test1List/upload"
            virtual="/test"
            :file-type="['txt']"
          />
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
import { listTest1List, getTest1List, delTest1List, addTest1List, updateTest1List } from "@/api/test1/test1List";
import MyUpload from "@/components/MyUpload/index.vue";
import {listCodeList} from "@/api/code/codeList";

export default {
  name: "Test1List",
  components: {MyUpload},
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
      // 测试列表表格数据
      test1ListList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      codeOptions:[],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        userId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        codeName: [
          { required: true, message: "所属代码不能为空", trigger: "blur" },
        ],
        path: [
          { required: true, message: "请上传文件", trigger: "blur" },
        ],
      }
    };
  },
  created() {
    this.getCode();
    this.getList();
  },
  methods: {
    /** 查询测试列表列表 */
    getList() {
      this.loading = true;
      listTest1List(this.queryParams).then(response => {
        this.test1ListList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getCode(){
      listCodeList().then(response => {
        this.codeOptions = this.getUniqueCodeOptions(response.rows);
      });
    },
    getUniqueCodeOptions(options) {
      const unique = new Set(options.map(item => item.name));
      return options.filter(item => unique.has(item.name));
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
        codeName:null
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
      this.title = "添加测试列表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getTest1List(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改测试列表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTest1List(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTest1List(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除测试列表编号为"' + ids + '"的数据项？').then(function() {
        return delTest1List(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('test1/test1List/export', {
        ...this.queryParams
      }, `test1List_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
