<script setup>
import { ref, onMounted, getCurrentInstance, inject } from "vue";
import { MainFeature } from "@/constants/MainFeature.js";
import { AccountType } from "@/constants/AccountType.js";
import { useRouter, useRoute } from "vue-router";

const { proxy } = getCurrentInstance();
const swal = inject("$swal");
const router = useRouter();
const route = useRoute();
const transferId = route.params.transferId;

const mainFeatureList = ref(MainFeature);
const feature = ref({
  id: 5,
  name: "Chuyển khoản",
  icon: ["fas", "right-left"],
  href: "/account/transfer/id",
});

const currentTime = ref(new Date());
const fromAccount = ref();
const toAccount = ref();
const dictionaryBucketPayment = ref([]);
const errMsg = ref("");

const expense = ref({
  amount: 0,
  dictionaryBucketPaymentId: "",
  expenseDate: "",
  beneficiaryAccountId: "",
  interpretation: "",
});

onMounted(() => {
  proxy.$api
    .get("/expense-regular/" + transferId)
    .then((res) => {
      expense.value = res.result;
      fromAccount.value = expense.value.dictionaryBucketPayment;
      fromAccount.value.accountType = AccountType.find(
        (type) => type.name === fromAccount.value.accountType
      );
      toAccount.value = expense.value.beneficiaryAccount;
      toAccount.value.accountType = AccountType.find(
        (type) => type.name === toAccount.value.accountType
      );
      currentTime.value = expense.value.expenseDate;
    })
    .catch((err) => {
      console.log(err);
    });
});


async function handleDeleteExpense() {
  swal
    .fire({
      title: "Lưu ý",
      text: "Dữ liệu bị xóa sẽ không thể khôi phục sau khi xóa. Bạn có muốn tiếp tục ?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      cancelButtonText: "Không",
      confirmButtonText: "Có",
      reverseButtons: true,
    })
    .then(async (result) => {
      if (result.isConfirmed) {
        await proxy.$api
          .delete("/expense-regular/transfer/" + transferId, {})
          .then(() => {
            swal.fire({
              title: "Thành công",
              text: "Bạn đã xóa thành công!",
              icon: "success",
            });
            router.push(route.query.redirect ? route.query.redirect : "/home");
          });
      }
    });
}
</script>

<template>
  <div class="transfer-info">
    <div class="transfer-header">
      <h2 class="text-primary">Thông tin chuyển khoản</h2>
      <div class="transfer-time">
        <el-date-picker
          v-model="currentTime"
          type="datetime"
          disabled
        />
      </div>
    </div>

    <div class="transfer-content">
      <div class="transfer-amount">
        <div class="amount-label">Số tiền</div>
        <div class="amount-value text-red-accent-3">
          {{ expense.amount.toLocaleString() }} ₫
        </div>
      </div>

      <div class="transfer-accounts">
        <div class="account-info from-account">
          <div class="account-label">Từ tài khoản</div>
          <div class="account-detail">
            <v-avatar>
              <img class="icon-size" :src="fromAccount?.accountType?.icon" alt="icon" />
            </v-avatar>
            <div class="account-name">{{ fromAccount?.accountName }}</div>
          </div>
        </div>

        <div class="transfer-icon">
          <font-awesome-icon :icon="['fas', 'arrow-right']" />
        </div>

        <div class="account-info to-account">
          <div class="account-label">Tới tài khoản</div>
          <div class="account-detail">
            <v-avatar>
              <img class="icon-size" :src="toAccount?.accountType?.icon" alt="icon" />
            </v-avatar>
            <div class="account-name">{{ toAccount?.accountName }}</div>
          </div>
        </div>
      </div>

      <div v-if="expense.interpretation" class="transfer-note">
        <div class="note-label">Diễn giải</div>
        <div class="note-content">{{ expense.interpretation }}</div>
      </div>
    </div>

    <div class="transfer-actions">
      <v-btn
        class="bg-white text-red-accent-3 border-sm mr-4"
        style="border-color: #ff1744 !important"
        @click="handleDeleteExpense"
      >
        <template v-slot:prepend>
          <font-awesome-icon :icon="['far', 'trash-can']" />
        </template>
        Xóa
      </v-btn>
      <v-btn
        color="primary"
        @click="router.push(route.query.redirect ? route.query.redirect : '/home')"
      >
        <template v-slot:prepend>
          <font-awesome-icon :icon="['fas', 'arrow-left']" />
        </template>
        Quay lại
      </v-btn>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.transfer-info {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.transfer-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 24px;
    margin-bottom: 16px;
  }

  .transfer-time {
    color: #666;
  }
}

.transfer-content {
  margin-bottom: 32px;
}

.transfer-amount {
  text-align: center;
  margin-bottom: 32px;
  padding: 24px;
  background: #f8f9fa;
  border-radius: 8px;

  .amount-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }

  .amount-value {
    font-size: 32px;
    font-weight: bold;
  }
}

.transfer-accounts {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  padding: 24px;
  background: #f8f9fa;
  border-radius: 8px;

  .account-info {
    flex: 1;
    
    .account-label {
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
    }

    .account-detail {
      display: flex;
      align-items: center;
    }

    .account-name {
      font-size: 16px;
      font-weight: 500;
    }
  }

  .transfer-icon {
    margin: 0 24px;
    color: #666;
    font-size: 20px;
  }
}

.transfer-note {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;

  .note-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }

  .note-content {
    font-size: 16px;
  }
}

.transfer-actions {
  text-align: center;

  .v-btn {
    min-width: 120px;
  }
}
</style>
