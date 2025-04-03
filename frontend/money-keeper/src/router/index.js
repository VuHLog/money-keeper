import { createRouter, createWebHistory } from "vue-router";
import { jwtDecode } from "jwt-decode";
import { useBaseStore } from "@/store/index.js";
import DefaultLayout from "@layouts/DefaultLayout.vue";
import AuthLayout from "@layouts/AuthLayout.vue";
import SignIn from "@pages/SignIn.vue";
import SignUp from "@pages/SignUp.vue";
import Home from "@pages/Home.vue";
import Account from "@pages/Account/Account.vue";
import AccountInfo from "@pages/Account/AccountInfo.vue";
import CreateAccount from "@pages/Account/CreateAccount.vue";
import AdjustedBalance from "@pages/Account/AdjustedBalance.vue";
import Transfer from "@pages/Account/Transfer.vue";
import Passbook from "@pages/Passbook/Passbook.vue";
import CreatePassbook from "@pages/Passbook/Create.vue";
import CreateExpense from "@pages/Expense/CreateExpense.vue";
import ExpenseInfo from "@pages/Expense/ExpenseInfo.vue";
import CreateRevenue from "@pages/Revenue/CreateRevenue.vue";
import RevenueInfo from "@pages/Revenue/RevenueInfo.vue";

const routes = [
  {
    path: "/",
    name: "/",
    redirect: "/home",
    component: DefaultLayout,
    meta: { requiresAuth: true },
    children: [
      { 
      path: "/home",
      name: "Home",
      component: Home
      },
      { 
        path: "/account",
        name: "Account",
        component: Account
      },
      { 
        path: "/account/info/:accountId",
        name: "Account Information",
        component: AccountInfo
      },
      { 
        path: "/account/create",
        name: "Create account",
        component: CreateAccount
      },
      { 
        path: "/account/adjusted-balance/:accountId",
        name: "Adjusted balance",
        component: AdjustedBalance
      },
      { 
        path: "/account/transfer/:accountId",
        name: "Transfer",
        component: Transfer
      },
      { 
        path: "/passbook",
        name: "Passbook",
        component: Passbook
      },
      { 
        path: "/passbook/create",
        name: "Create passbook",
        component: CreatePassbook
      },
      {
        path: "/expense/create",
        name: "Create expense",
        component: CreateExpense
      },
      {
        path: "/expense/:expenseId",
        name: "Expense info",
        component: ExpenseInfo
      },
      {
        path: "/revenue/create",
        name: "Create revenue",
        component: CreateRevenue
      },
      {
        path: "/revenue/:revenueId",
        name: "Revenue info",
        component: RevenueInfo
      },
    ],
  },
  {
    path: "/auth",
    name: "Auth",
    component: AuthLayout,
    children: [
      {
        path: "sign-in",
        name: "Sign In",
        component: SignIn,
      },
      {
        path: "sign-up",
        name: "Sign Up",
        component: SignUp,
      },
    ],
  },

  {
    path: "/:pathMatch(.*)*",
    redirect: "/home",
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  linkActiveClass: "active",
});

router.beforeEach((to, from, next) => {
  const store = useBaseStore();
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!isLoggedIn() || !store.isLoggedIn) {
      next({
        path: "/auth/sign-in",
        query: { redirect: to.fullPath },
      });
    } else {
      next();
    }
  } else {
    next();
  }
});

//hàm check login với token
function isLoggedIn() {
  const store = useBaseStore();
  const token = sessionStorage.getItem("token");

  if (token) {
    //giải mã token
    const decoded = jwtDecode(token);
    store.roles = decoded.scope.replaceAll("ROLE_", "");
    store.username = decoded.sub;
    store.fullName = decoded.name;

    // Kiểm tra xem token có hết hạn hay không
    const expirationDate = new Date(decoded.exp * 1000);
    if (expirationDate <= new Date()) {
      // Nếu token đã hết hạn, xóa nó khỏi sessionStorage và trả về false
      sessionStorage.removeItem("token");
      return false;
    } else {
      // Nếu token hợp lệ, trả về true
      return true;
    }
  } else {
    // Nếu token không tồn tại trong sessionStorage, trả về false
    return false;
  }
}

export default router;
