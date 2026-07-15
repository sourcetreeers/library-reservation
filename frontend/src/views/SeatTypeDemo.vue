<template>
  <div class="seat-type-demo">
    <h2>座位类型可视化演示</h2>
    
    <!-- 座位类型图例 -->
    <div class="seat-legend">
      <div class="legend-item">
        <div class="legend-icon normal-seat"></div>
        <span>普通座位</span>
      </div>
      <div class="legend-item">
        <div class="legend-icon computer-seat"></div>
        <span>电脑座位</span>
      </div>
      <div class="legend-item">
        <div class="legend-icon quiet-seat"></div>
        <span>静音座位</span>
      </div>
    </div>

    <!-- 按座位类型分组显示 -->
    <div class="seats-by-type">
      <!-- 普通座位区域 -->
      <div class="seat-type-section">
        <h4 class="seat-type-title">
          <span class="type-icon normal-seat"></span>
          普通座位区 ({{ normalSeats.length }}个)
        </h4>
        <div class="seat-grid">
          <div
            v-for="seat in normalSeats"
            :key="seat.id"
            class="seat-item normal-seat"
            :class="{ selected: selectedSeat && selectedSeat.id === seat.id }"
            @click="selectSeat(seat)"
          >
            {{ seat.seatNumber }}
          </div>
        </div>
      </div>

      <!-- 电脑座位区域 -->
      <div class="seat-type-section">
        <h4 class="seat-type-title">
          <span class="type-icon computer-seat"></span>
          电脑座位区 ({{ computerSeats.length }}个)
        </h4>
        <div class="seat-grid">
          <div
            v-for="seat in computerSeats"
            :key="seat.id"
            class="seat-item computer-seat"
            :class="{ selected: selectedSeat && selectedSeat.id === seat.id }"
            @click="selectSeat(seat)"
          >
            {{ seat.seatNumber }}
          </div>
        </div>
      </div>

      <!-- 静音座位区域 -->
      <div class="seat-type-section">
        <h4 class="seat-type-title">
          <span class="type-icon quiet-seat"></span>
          静音座位区 ({{ quietSeats.length }}个)
        </h4>
        <div class="seat-grid">
          <div
            v-for="seat in quietSeats"
            :key="seat.id"
            class="seat-item quiet-seat"
            :class="{ selected: selectedSeat && selectedSeat.id === seat.id }"
            @click="selectSeat(seat)"
          >
            {{ seat.seatNumber }}
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedSeat" class="selected-info">
      <h3>已选择座位</h3>
      <p>座位编号: {{ selectedSeat.seatNumber }}</p>
      <p>座位类型: {{ selectedSeat.seatType }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SeatTypeDemo',
  data() {
    return {
      selectedSeat: null,
      // 模拟座位数据
      availableSeats: [
        { id: 1, seatNumber: 'A01', seatType: '普通座位' },
        { id: 2, seatNumber: 'A02', seatType: '普通座位' },
        { id: 3, seatNumber: 'A03', seatType: '电脑座位' },
        { id: 4, seatNumber: 'A04', seatType: '电脑座位' },
        { id: 5, seatNumber: 'B01', seatType: '普通座位' },
        { id: 6, seatNumber: 'B02', seatType: '普通座位' },
        { id: 7, seatNumber: 'B03', seatType: '电脑座位' },
        { id: 8, seatNumber: 'B04', seatType: '电脑座位' },
        { id: 9, seatNumber: 'C01', seatType: '普通座位' },
        { id: 10, seatNumber: 'C02', seatType: '普通座位' },
        { id: 11, seatNumber: 'C03', seatType: '电脑座位' },
        { id: 12, seatNumber: 'C04', seatType: '电脑座位' },
        { id: 13, seatNumber: 'D01', seatType: '静音座位' },
        { id: 14, seatNumber: 'D02', seatType: '静音座位' },
        { id: 15, seatNumber: 'D03', seatType: '静音座位' },
        { id: 16, seatNumber: 'D04', seatType: '静音座位' }
      ]
    }
  },
  computed: {
    // 按座位类型分组
    normalSeats() {
      return this.availableSeats.filter(seat => seat.seatType === '普通座位')
    },
    computerSeats() {
      return this.availableSeats.filter(seat => seat.seatType === '电脑座位')
    },
    quietSeats() {
      return this.availableSeats.filter(seat => seat.seatType === '静音座位')
    }
  },
  methods: {
    selectSeat(seat) {
      this.selectedSeat = seat
    }
  }
}
</script>

<style scoped>
.seat-type-demo {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

/* 座位类型图例 */
.seat-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.legend-icon {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 2px solid;
}

/* 座位类型分组 */
.seats-by-type {
  margin-bottom: 20px;
}

.seat-type-section {
  margin-bottom: 25px;
}

.seat-type-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 500;
  color: #323233;
}

.type-icon {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  border: 2px solid;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.seat-item {
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  position: relative;
  border: 2px solid;
}

/* 普通座位样式 */
.seat-item.normal-seat {
  background: #f0f9ff;
  border-color: #0ea5e9;
  color: #0369a1;
}

.seat-item.normal-seat:hover {
  background: #e0f2fe;
  border-color: #0284c7;
}

.seat-item.normal-seat.selected {
  background: #0ea5e9;
  color: white;
  border-color: #0284c7;
}

.legend-icon.normal-seat,
.type-icon.normal-seat {
  background: #f0f9ff;
  border-color: #0ea5e9;
}

/* 电脑座位样式 */
.seat-item.computer-seat {
  background: #f0fdf4;
  border-color: #22c55e;
  color: #15803d;
}

.seat-item.computer-seat:hover {
  background: #dcfce7;
  border-color: #16a34a;
}

.seat-item.computer-seat.selected {
  background: #22c55e;
  color: white;
  border-color: #16a34a;
}

.legend-icon.computer-seat,
.type-icon.computer-seat {
  background: #f0fdf4;
  border-color: #22c55e;
}

/* 为电脑座位添加图标 */
.seat-item.computer-seat::before {
  content: '💻';
  position: absolute;
  top: 3px;
  right: 5px;
  font-size: 12px;
}

/* 静音座位样式 */
.seat-item.quiet-seat {
  background: #fef3f2;
  border-color: #f97316;
  color: #c2410c;
}

.seat-item.quiet-seat:hover {
  background: #fed7d1;
  border-color: #ea580c;
}

.seat-item.quiet-seat.selected {
  background: #f97316;
  color: white;
  border-color: #ea580c;
}

.legend-icon.quiet-seat,
.type-icon.quiet-seat {
  background: #fef3f2;
  border-color: #f97316;
}

/* 为静音座位添加图标 */
.seat-item.quiet-seat::before {
  content: '🤫';
  position: absolute;
  top: 3px;
  right: 5px;
  font-size: 12px;
}

/* 为电脑座位添加图标 */
.seat-item.computer-seat::before {
  content: '💻';
  position: absolute;
  top: 3px;
  right: 5px;
  font-size: 12px;
}

/* 为普通座位添加图标 */
.seat-item.normal-seat::before {
  content: '📚';
  position: absolute;
  top: 3px;
  right: 5px;
  font-size: 12px;
}

.selected-info {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
}

.selected-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.selected-info p {
  margin: 5px 0;
  color: #666;
}
</style>