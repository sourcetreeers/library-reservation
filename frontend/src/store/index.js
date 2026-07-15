import Vue from 'vue'
import Vuex from 'vuex'
import { getCurrentUser, setCurrentUser, clearCurrentUser } from '@/utils/auth'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: getCurrentUser(), // 当前用户信息
    libraries: [], // 图书馆列表
    selectedLibrary: null, // 选中的图书馆
    selectedSeat: null, // 选中的座位
    reservationTime: null // 预约时间
  },
  
  mutations: {
    SET_USER(state, user) {
      state.user = user
      if (user) {
        setCurrentUser(user)
      } else {
        clearCurrentUser()
      }
    },
    
    SET_LIBRARIES(state, libraries) {
      state.libraries = libraries
    },
    
    SET_SELECTED_LIBRARY(state, library) {
      state.selectedLibrary = library
    },
    
    SET_SELECTED_SEAT(state, seat) {
      state.selectedSeat = seat
    },
    
    SET_RESERVATION_TIME(state, time) {
      state.reservationTime = time
    },
    
    CLEAR_RESERVATION_DATA(state) {
      state.selectedLibrary = null
      state.selectedSeat = null
      state.reservationTime = null
    }
  },
  
  actions: {
    // 设置用户信息
    setUser({ commit }, user) {
      commit('SET_USER', user)
    },
    
    // 清除用户信息
    clearUser({ commit }) {
      commit('SET_USER', null)
    },
    
    // 设置图书馆列表
    setLibraries({ commit }, libraries) {
      commit('SET_LIBRARIES', libraries)
    },
    
    // 设置选中的图书馆
    setSelectedLibrary({ commit }, library) {
      commit('SET_SELECTED_LIBRARY', library)
    },
    
    // 设置选中的座位
    setSelectedSeat({ commit }, seat) {
      commit('SET_SELECTED_SEAT', seat)
    },
    
    // 设置预约时间
    setReservationTime({ commit }, time) {
      commit('SET_RESERVATION_TIME', time)
    },
    
    // 清除预约数据
    clearReservationData({ commit }) {
      commit('CLEAR_RESERVATION_DATA')
    }
  },
  
  getters: {
    isLoggedIn: state => !!state.user,
    isAdmin: state => state.user && (state.user.userType === '图书馆管理员' || state.user.userType === '管理员'),
    isSystemAdmin: state => state.user && state.user.userType === '系统管理员',
    isStudent: state => state.user && state.user.userType === '学生'
  }
})