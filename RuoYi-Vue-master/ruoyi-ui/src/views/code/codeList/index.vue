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
<!--      <el-form-item label="代码类型" prop="type">-->
<!--        <el-input-->
<!--          v-model="queryParams.type"-->
<!--          placeholder="请输入代码类型"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
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
          v-hasPermi="['code:codeList:add']"
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
          v-hasPermi="['code:codeList:edit']"
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
          v-hasPermi="['code:codeList:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['code:codeList:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="codeListList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="代码id" align="center" prop="id" />-->
      <el-table-column label="代码名称" align="center" prop="name" />
      <el-table-column label="代码版本" align="center" prop="version" />
      <el-table-column label="代码类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.code_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="导入人员" align="center" prop="importUser" />
      <el-table-column label="导入时间" align="center" prop="time" width="180">
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.time, '{y}-{m}-{d}') }}</span>-->
<!--        </template>-->
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="openFile(scope.row.path)"
          >查看文件
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['code:codeList:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['code:codeList:remove']"
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

    <!-- 添加或修改代码列表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="代码名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入代码名称" :disabled="isAdd"/>
        </el-form-item>
        <el-form-item label="代码类型" prop="type" v-if="form.type===null">
          <el-select v-model="form.type" placeholder="请选择代码类型" @change="handleChange">
            <el-option
              v-for="dict in dict.type.code_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item v-if="form.type!==null" label="代码文件" prop="path">
         <my-upload
           v-model="form.path"
           :originalFilename.sync="form.name"
           path="/code/codeList/upload"
           virtual="/code"
           :file-type="fileType"
         />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="form.path!==null" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCodeList, getCodeList, delCodeList, addCodeList, updateCodeList } from "@/api/code/codeList";
import MyUpload from "@/components/MyUpload/index.vue";

export default {
  name: "CodeList",
  components: {MyUpload},
  dicts:['code_type'],
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
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        // name: [
        //   { required: true, message: "名称不能为空", trigger: "blur" },
        // ],
        type: [
          { required: true, message: "代码类型不能为空", trigger: "blur" },
        ],
        path: [
          { required: true, message: "请上传文件", trigger: "blur" },
        ],
      }
    };
  },

  created() {
    this.getList();
  },

  methods: {
    openFile(fileName){
      window.open(process.env.VUE_APP_BASE_API+'/code/'+fileName, '_blank');
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
    handleChange() {
      if(this.form.type==='2'){
        this.fileType=['c']
      }
      if(this.form.type==='1'){
        this.fileType=['py','zip']
      }
      if (this.form.type==='0'){
        this.fileType=['java','zip']
      }
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.isAdd = true;
      this.open = true;
      this.title = "添加代码列表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.isAdd = false;
      const id = row.id || this.ids
      getCodeList(id).then(response => {
        this.form = response.data;
        this.form.type = this.form.type.toString();
        this.open = true;
        this.title = "修改代码列表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCodeList(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCodeList(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除代码列表编号为"' + ids + '"的数据项？').then(function() {
        return delCodeList(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('code/codeList/export', {
        ...this.queryParams
      }, `codeList_${new Date().getTime()}.xlsx`)
    }
  }

};
</script>
