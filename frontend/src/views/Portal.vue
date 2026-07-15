<template>
  <div class="portal-page">
    <div class="portal-container">
      <div class="portal-header">
        <svg class="portal-logo" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
          <polyline points="9 22 9 12 15 12 15 22"/>
        </svg>
        <h1>图书馆座位预约系统</h1>
        <p>Library Seat Reservation System</p>
      </div>
      
      <div class="portal-cards">
        <!-- 学生端 -->
        <div class="portal-card" @click="goToMobile">
          <div class="card-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="#007AFF" stroke-width="1.5">
              <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
              <path d="M6 12v5c0 1.66 2.69 3 6 3s6-1.34 6-3v-5"/>
            </svg>
          </div>
          <h2>学生端</h2>
          <p>预约座位、查看预约记录、管理个人积分</p>
          <div class="card-arrow">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
        
        <!-- 图书馆管理员端 -->
        <div class="portal-card" @click="goToLibrarian">
          <div class="card-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="#34C759" stroke-width="1.5">
              <path d="M4 19.5A2.5 2.5 0 016.5 17H20"/>
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 014 19.5v-15A2.5 2.5 0 016.5 2z"/>
            </svg>
          </div>
          <h2>图书馆管理员端</h2>
          <p>管理图书室、座位、预约和扫码验证</p>
          <div class="card-arrow">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </div>
        </div>

        <!-- 系统管理员端 -->
        <div class="portal-card" @click="goToSystemAdmin">
          <div class="card-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="#8E8E93" stroke-width="1.5">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/>
            </svg>
          </div>
          <h2>系统管理员端</h2>
          <p>用户管理、规则配置、积分记录总览</p>
          <div class="card-arrow">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </div>
        </div>
      </div>
      
      <div class="portal-footer">
        <p>&copy; 2026 Library Reservation System. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Portal',
  
  methods: {
    goToMobile() {
      this.$router.push('/mobile/login')
    },
    
    goToLibrarian() {
      this.$router.push('/admin/login')
    },
    
    goToSystemAdmin() {
      this.$router.push('/system-admin/login')
    }
  }
}
</script>

<style scoped>
.portal-page {
  min-height: 100vh;
  background: #F5F5F7;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
}

.portal-container {
  max-width: 960px;
  width: 100%;
}

.portal-header {
  text-align: center;
  margin-bottom: 48px;
}

.portal-logo {
  width: 48px;
  height: 48px;
  color: #007AFF;
  margin-bottom: 16px;
}

.portal-header h1 {
  margin: 0 0 8px;
  font-size: 40px;
  font-weight: 700;
  color: #1D1D1F;
  letter-spacing: -0.5px;
}

.portal-header p {
  margin: 0;
  font-size: 17px;
  color: #86868B;
  letter-spacing: 0.5px;
}

.portal-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 48px;
}

.portal-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 36px 28px 28px;
  text-align: center;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  position: relative;
}

.portal-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #007AFF;
}

.card-icon-wrap {
  width: 56px;
  height: 56px;
  margin: 0 auto 20px;
  background: #F5F5F7;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon-wrap svg {
  width: 28px;
  height: 28px;
}

.portal-card h2 {
  margin: 0 0 10px;
  font-size: 20px;
  font-weight: 600;
  color: #1D1D1F;
}

.portal-card p {
  margin: 0 0 20px;
  color: #86868B;
  font-size: 14px;
  line-height: 1.5;
}

.card-arrow {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  transition: all 0.2s ease;
}

.card-arrow svg {
  width: 16px;
  height: 16px;
  color: #8E8E93;
}

.portal-card:hover .card-arrow {
  background: #007AFF;
}

.portal-card:hover .card-arrow svg {
  color: #FFFFFF;
}

.portal-footer {
  text-align: center;
}

.portal-footer p {
  margin: 0;
  font-size: 13px;
  color: #AEAEB2;
}

@media (max-width: 768px) {
  .portal-header h1 {
    font-size: 30px;
  }
  
  .portal-cards {
    grid-template-columns: 1fr;
    max-width: 360px;
    margin-left: auto;
    margin-right: auto;
  }
}
</style>
