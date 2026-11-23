<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showNotify } from 'vant'
import { resetPasswordService, sendResetCodeService } from '../apis/user'

const router = useRouter()
const email = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const sending = ref(false)
const submitting = ref(false)

const sendCode = async () => {
  if (!email.value.trim()) {
    showNotify({ type: 'danger', message: '请输入已注册邮箱' })
    return
  }
  sending.value = true
  try {
    await sendResetCodeService(email.value.trim())
    showNotify({ type: 'success', message: '验证码已发送，请查收邮件或开发日志' })
  } finally {
    sending.value = false
  }
}
const reset = async () => {
  if (newPassword.value !== confirmPassword.value) {
    showNotify({ type: 'danger', message: '两次输入的新密码不一致' })
    return
  }
  submitting.value = true
  try {
    await resetPasswordService({
      email: email.value.trim(),
      code: code.value.trim(),
      newPassword: newPassword.value
    })
    showNotify({ type: 'success', message: '密码已重置，请重新登录' })
    router.replace('/login')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div>
    <van-nav-bar title="找回密码" left-text="返回" left-arrow @click-left="router.back()" />
    <div class="tip">验证码 10 分钟内有效，同一邮箱 60 秒内只能发送一次。</div>
    <van-form @submit="reset">
      <van-cell-group inset>
        <van-field v-model.trim="email" label="邮箱" type="email" placeholder="请输入注册邮箱" />
        <van-field v-model.trim="code" label="验证码" maxlength="6" placeholder="6 位数字">
          <template #button>
            <van-button size="small" type="primary" :loading="sending" @click="sendCode">发送验证码</van-button>
          </template>
        </van-field>
        <van-field v-model="newPassword" label="新密码" type="password" placeholder="6-64 位" />
        <van-field v-model="confirmPassword" label="确认密码" type="password" placeholder="再次输入新密码" />
      </van-cell-group>
      <div class="actions">
        <van-button block round type="primary" native-type="submit" :loading="submitting">重置密码</van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.tip { padding: 18px; color: #777; line-height: 1.6; }
.actions { margin: 24px 16px; }
</style>
