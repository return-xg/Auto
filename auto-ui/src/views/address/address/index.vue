<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
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
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="addressList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="收货人姓名" align="center" prop="consignee" />
      <el-table-column label="手机号" align="center" prop="phone" />
      <el-table-column label="省" align="center" prop="province" />
      <el-table-column label="市" align="center" prop="city" />
      <el-table-column label="区/县" align="center" prop="district" />
      <el-table-column label="详细地址" align="center" prop="detail" />
      <el-table-column label="是否默认地址" align="center" prop="isDefault" width="120">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isDefault"
            :active-value="true"
            :inactive-value="false"
            @change="handleDefaultChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 添加或修改收货地址对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="form.consignee" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省市区" prop="region" required>
          <el-cascader
            v-model="form.region"
            :options="regionData"
            :props="{ expandTrigger: 'hover' }"
            placeholder="请选择省/市/区"
            style="width: 100%;"
            @change="handleRegionChange"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="是否默认地址" prop="isDefault">
          <el-switch v-model="form.isDefault" />
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
import { listAddress, getAddress, delAddress, addAddress, updateAddress } from "@/api/address/address"
import { regionData, CodeToText } from "element-china-area-data"
import { mapGetters } from "vuex"

export default {
  name: "Address",
  computed: {
    ...mapGetters(['id'])
  },
  data() {
    return {
      regionData: regionData,
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      addressList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      form: {},
      rules: {
        consignee: [
          { required: true, message: "收货人姓名不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "手机号不能为空", trigger: "blur" }
        ],
        region: [
          { 
            required: true, 
            message: "请选择省市区", 
            trigger: "change", 
            type: 'array',
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                callback(new Error('请选择省市区'))
              } else {
                callback()
              }
            }
          }
        ],
        detail: [
          { required: true, message: "详细地址不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询收货地址列表 */
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        userId: this.id
      }
      listAddress(params).then(response => {
        this.addressList = response.rows.map(item => ({
          ...item,
          isDefault: item.isDefault === 1 || item.isDefault === true
        }))
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        userId: null,
        consignee: null,
        phone: null,
        region: [],
        province: null,
        city: null,
        district: null,
        detail: null,
        isDefault: false,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加收货地址"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAddress(id).then(response => {
        this.form = response.data
        this.form.region = this.findRegionCodes(response.data.province, response.data.city, response.data.district)
        this.form.isDefault = response.data.isDefault === 1 || response.data.isDefault === true
        this.open = true
        this.title = "修改收货地址"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const submitData = {
            ...this.form,
            userId: this.id,
            isDefault: this.form.isDefault ? 1 : 0
          }
          if (this.form.id != null) {
            updateAddress(submitData).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAddress(submitData).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleRegionChange(value) {
      if (value && value.length === 3) {
        this.form.province = CodeToText[value[0]]
        this.form.city = CodeToText[value[1]]
        this.form.district = CodeToText[value[2]]
      }
    },
    handleDefaultChange(row) {
      const submitData = {
        ...row,
        isDefault: row.isDefault ? 1 : 0
      }
      updateAddress(submitData).then(() => {
        this.$modal.msgSuccess(row.isDefault ? "已设置为默认地址" : "已取消默认地址")
        this.getList()
      }).catch(() => {
        this.getList()
      })
    },
    findRegionCodes(province, city, district) {
      let provinceCode = null
      let cityCode = null
      let districtCode = null

      for (const p of regionData) {
        if (p.label === province) {
          provinceCode = p.value
          if (p.children && p.children.length > 0) {
            for (const c of p.children) {
              if (c.label === city) {
                cityCode = c.value
                if (c.children && c.children.length > 0) {
                  for (const d of c.children) {
                    if (d.label === district) {
                      districtCode = d.value
                      break
                    }
                  }
                }
                break
              }
            }
          }
          break
        }
      }

      return [provinceCode, cityCode, districtCode]
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除收货地址编号为"' + ids + '"的数据项？').then(function() {
        return delAddress(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('address/address/export', {
        ...this.queryParams
      }, `address_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
