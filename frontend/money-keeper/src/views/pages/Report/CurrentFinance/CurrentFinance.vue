<script setup>
import { ref, onMounted } from "vue";
import { formatCurrency } from "@/utils/Format";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment";

const dictionaryBucketPaymentStore = useDictionaryBucketPaymentStore();

const bucketPayments = ref([]);
const totalMoney = ref(0);

onMounted(async () => {
    await getData();
});

async function getData() {
    bucketPayments.value = await dictionaryBucketPaymentStore.getMyBucketPayments();
    totalMoney.value = bucketPayments.value.reduce((acc, item) => {
        return acc + item.balance;
    }, 0);
}
</script>

<template>
    <div class="current-finance-container">
        <!-- Tài chính hiện tại -->
        <!-- <div class="section">
      <div class="section-header">
        <span class="title">Tài chính hiện tại (1) - (2)</span>
        <span class="amount">{{ formatCurrency(currentFinance.netFinance) }}</span>
      </div>
    </div> -->

    <div class="section" v-if="bucketPayments.length > 0">
        <div class="section-header">
            <span class="title">Tổng có</span>
            <span class="amount">{{ formatCurrency(totalMoney) }}</span>
        </div>
        <div class="details">
            <table class="details-table">
                <tbody>
                    <tr v-for="(item, index) in bucketPayments" :key="index">
                        <router-link class="text-decoration-none" :to="'/account/info/' + item.id">
                            <td class="detail-item justify-space-between">
                                <div class="d-flex align-center">
                                    <v-avatar>
                                        <img class="icon-size" :src="item.iconUrl" :alt="item.accountName" />
                                    </v-avatar>
                                    <span class="name">{{ item.accountName }}</span>
                                </div>
                                <div :class="item.balance < 0 ? 'text-red-accent-3' : 'text-primary'">
                                    {{ formatCurrency(item.balance) }}
                                </div>
                            </td>
                        </router-link>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div v-else class="d-flex justify-center align-center flex-column flex-grow-1">
      <font-awesome-icon icon="fa-solid fa-circle-exclamation" size="4x" class="text-grey mb-4" />
      <h2 class="text-h5 text-grey">Không có tài khoản nào</h2>
      <p class="text-subtitle-1 text-grey">Hãy tạo một tài khoản mới để bắt đầu thực hiện chi tiêu của bạn.</p>
      <v-btn class="mt-4" color="primary" to="/account/create">Tạo tài khoản mới</v-btn>
    </div>

        <!-- <div class="section">
      <div class="section-header">
        <span class="title">Tổng nợ (2)</span>
        <span class="amount">{{ formatCurrency(currentFinance.totalDebts) }}</span>
      </div>
    </div> -->
    </div>
</template>

<style lang="scss" scoped>
.current-finance-container {
    padding: 24px;
    font-family: Arial, sans-serif;
    max-width: 1200px;
    margin: 0 auto;
}

.section {
    margin-bottom: 24px;
    background-color: #fff;
    border-radius: 8px;
    padding: 16px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
    padding-bottom: 8px;
    margin-bottom: 16px;
}

.title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
}

.amount {
    font-size: 18px;
    font-weight: bold;
    color: #333;
}

.details-table {
    width: 100%;
    border-collapse: collapse;
}

.details-table th,
.details-table td {
    text-align: left;
    padding: 12px;
    border-bottom: 1px solid #eee;
}

.details-table th {
    background-color: #f9f9f9;
    font-size: 14px;
    font-weight: bold;
}

.detail-item {
    display: flex;
    align-items: center;
    gap: 8px;
    &:hover {
        background-color: #EEEEEE;
    }
}

.icon {
    font-size: 20px;
    color: #666;
}

.name {
    font-size: 14px;
    color: #333;
}
</style>