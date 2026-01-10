<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="门店名称">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入门店名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省">
        <el-select
          v-model="queryParams.province"
          placeholder="请选择省份"
          clearable
          @change="onProvinceChange"
        >
          <el-option
            v-for="province in provinces"
            :key="province.value"
            :label="province.label"
            :value="province.label"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="市">
        <el-select
          v-model="queryParams.city"
          placeholder="请选择城市"
          clearable
          :disabled="!queryParams.province"
          @change="onQueryCityChange"
        >
          <el-option
            v-for="city in cities"
            :key="city.value"
            :label="city.label"
            :value="city.label"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="区/县">
        <el-select
          v-model="queryParams.district"
          placeholder="请选择区/县"
          clearable
          :disabled="!queryParams.city"
        >
          <el-option
            v-for="district in districts"
            :key="district.value"
            :label="district.label"
            :value="district.label"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="营业" :value="1" />
          <el-option label="停业" :value="0" />
        </el-select>
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
      :data="storeList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="门店编号" align="center" prop="id" width="80" />
      <el-table-column label="门店名称" align="center" prop="name" width="150" show-overflow-tooltip />
      <el-table-column label="省份" align="center" prop="province" width="100" />
      <el-table-column label="城市" align="center" prop="city" width="100" />
      <el-table-column label="区/县" align="center" prop="district" width="100" />
      <el-table-column label="详细地址" align="center" prop="address" width="200" show-overflow-tooltip />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="营业时间" align="center" prop="businessHours" width="150" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '营业' : '停业' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
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
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="门店名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入门店名称" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-select v-model="form.province" placeholder="请选择省份" @change="onFormProvinceChange">
            <el-option
              v-for="province in provinces"
              :key="province.value"
              :label="province.label"
              :value="province.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-select v-model="form.city" placeholder="请选择城市" :disabled="!form.province" @change="onFormCityChange">
            <el-option
              v-for="city in cities"
              :key="city.value"
              :label="city.label"
              :value="city.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="区/县" prop="district">
          <el-select v-model="form.district" placeholder="请选择区/县" :disabled="!form.city">
            <el-option
              v-for="district in districts"
              :key="district.value"
              :label="district.label"
              :value="district.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHours">
          <el-input v-model="form.businessHours" placeholder="请输入营业时间" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">营业</el-radio>
            <el-radio :label="0">停业</el-radio>
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
import { listStore, getStore, addStore, updateStore, delStore } from '@/api/store/store'
import { regionData } from 'element-china-area-data'

export default {
  name: 'Store',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      storeList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        province: undefined,
        city: undefined,
        district: undefined,
        phone: undefined,
        status: undefined
      },
      form: {},
      rules: {
        name: [
          { required: true, message: '门店名称不能为空', trigger: 'blur' }
        ],
        province: [
          { required: true, message: '省份不能为空', trigger: 'change' }
        ],
        city: [
          { required: true, message: '城市不能为空', trigger: 'change' }
        ],
        district: [
          { required: true, message: '区/县不能为空', trigger: 'change' }
        ],
        address: [
          { required: true, message: '详细地址不能为空', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '联系电话不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ]
      },
      provinces: [],
      cities: [],
      districts: []
    }
  },
  created() {
    this.initRegionData()
    this.getList()
  },
  methods: {
    initRegionData() {
      this.provinces = regionData.map(province => ({
        value: province.value,
        label: province.label
      }))
    },
    getList() {
      this.loading = true
      listStore(this.queryParams).then(response => {
        this.storeList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(error => {
        this.loading = false
        this.$message.error('获取门店列表失败：' + error.message)
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.cities = []
      this.districts = []
      this.queryParams.district = undefined
      this.handleQuery()
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.handleQuery()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加门店'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getStore(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改门店'
        if (this.form.province) {
          this.loadCities(this.form.province)
          if (this.form.city) {
            this.loadDistricts(this.form.province, this.form.city)
          }
        }
      }).catch(error => {
        this.$message.error('获取门店详情失败：' + error.message)
      })
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStore(this.form).then(response => {
              this.$message.success('修改成功')
              this.open = false
              this.getList()
            }).catch(error => {
              this.$message.error('修改失败：' + error.message)
            })
          } else {
            addStore(this.form).then(response => {
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
    reset() {
      this.form = {
        id: null,
        name: null,
        province: null,
        city: null,
        district: null,
        address: null,
        phone: null,
        businessHours: null,
        status: 1
      }
      this.cities = []
      this.districts = []
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$confirm('是否确认删除门店编号为"' + ids + '"的数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delStore(ids)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      }).catch(error => {
        this.$message.error('删除失败：' + error.message)
      })
    },
    handleBatchDelete() {
      this.$confirm('是否确认删除选中的门店?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delStore(this.ids)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      }).catch(error => {
        this.$message.error('删除失败：' + error.message)
      })
    },
    onProvinceChange() {
      this.queryParams.city = ''
      this.queryParams.district = ''
      this.cities = []
      this.districts = []
      if (this.queryParams.province) {
        this.loadCities(this.queryParams.province)
      }
    },
    onQueryCityChange() {
      this.queryParams.district = ''
      this.districts = []
      if (this.queryParams.city) {
        this.loadDistricts(this.queryParams.province, this.queryParams.city)
      }
    },
    onFormProvinceChange() {
      this.form.city = ''
      this.cities = []
      this.form.district = ''
      this.districts = []
      if (this.form.province) {
        this.loadCities(this.form.province)
      }
    },
    onFormCityChange() {
      this.form.district = ''
      this.districts = []
      if (this.form.city) {
        this.loadDistricts(this.form.province, this.form.city)
      }
    },
    loadCities(province) {
      const provinceData = regionData.find(p => p.label === province)
      if (provinceData && provinceData.children) {
        this.cities = provinceData.children.map(city => ({
          value: city.value,
          label: city.label
        }))
      } else {
        this.cities = []
      }
    },
    loadDistricts(province, city) {
      const provinceData = regionData.find(p => p.label === province)
      if (provinceData && provinceData.children) {
        const cityData = provinceData.children.find(c => c.label === city)
        if (cityData && cityData.children) {
          this.districts = cityData.children.map(district => ({
            value: district.value,
            label: district.label
          }))
          return
        }
      }
      this.districts = []
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
