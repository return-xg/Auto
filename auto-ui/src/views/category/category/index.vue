<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="分类名称">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入分类名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="success" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
        <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleBatchDelete" :disabled="multiple">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="categoryList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分类ID" align="center" prop="id" width="80" />
      <el-table-column label="分类名称" align="center" prop="name" width="180" />
      <el-table-column label="是否显示" align="center" prop="isShow" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isShow"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="primary"
            icon="el-icon-edit"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            type="danger"
            icon="el-icon-delete"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size.sync="queryParams.pageSize"
        :current-page.sync="queryParams.pageNum"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="是否显示" prop="isShow">
          <el-radio-group v-model="form.isShow">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCategory, getCategory, addCategory, updateCategory, deleteCategory } from '@/api/category/category'

export default {
  name: 'Category',
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
      // 商品分类表格数据
      categoryList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: '分类名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询商品分类列表 */
    getList() {
      this.loading = true
      listCategory(this.queryParams).then(response => {
        this.categoryList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(error => {
        this.loading = false
        this.$message.error('获取分类列表失败：' + error.message)
      })
    },
    // 搜索按钮操作
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置按钮操作
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 表格大小变化
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.handleQuery()
    },
    // 表格页码变化
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 是否显示修改
    handleStatusChange(row) {
      const that = this
      this.$confirm('确认要修改该分类的显示状态吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return updateCategory(row)
      }).then(() => {
        that.getList()
        that.$message.success('修改成功')
      }).catch(error => {
        that.getList()
        that.$message.error('修改失败：' + error.message)
      })
    },
    // 新增按钮操作
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加商品分类'
    },
    // 修改按钮操作
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getCategory(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改商品分类'
      }).catch(error => {
        this.$message.error('获取分类详情失败：' + error.message)
      })
    },
    // 提交按钮
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCategory(this.form).then(response => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            }).catch(error => {
              this.$message.error('修改失败：' + error.message)
            })
          } else {
            addCategory(this.form).then(response => {
              this.$message.success('新增成功')
              this.open = false
              this.getList()
            }).catch(error => {
              this.$message.error('新增失败：' + error.message)
            })
          }
        }
      })
    },
    // 重置表单
    reset() {
      this.form = {
        id: null,
        name: null,
        isShow: 1
      }
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 删除按钮操作
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$confirm('是否确认删除商品分类编号为"' + ids + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteCategory(ids)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      }).catch(error => {
        this.$message.error('删除失败：' + error.message)
      })
    },
    // 批量删除按钮操作
    handleBatchDelete() {
      this.$confirm('是否确认删除选中的商品分类?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteCategory(this.ids)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      }).catch(error => {
        this.$message.error('删除失败：' + error.message)
      })
    }
  }
}
</script>

<style scoped>
.pagination-container {
  padding: 32px 16px;
  text-align: right;
}
</style>