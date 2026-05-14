export const RoomStatus = {
  CLEAN: 'CLEAN',
  RESERVED: 'RESERVED',
  BOOKED: 'BOOKED',
  OCCUPIED: 'OCCUPIED',
  DIRTY: 'DIRTY',
  OUT_OF_SERVICE: 'OUT_OF_SERVICE',
} as const

export type RoomStatus = (typeof RoomStatus)[keyof typeof RoomStatus]

export interface FloorVO {
  id: number
  name: string
  floorNo: number
}

export interface RoomAssignmentVO {
  id: number
  guestName: string
  guestPhone: string
  checkInTime: string
  checkOutTime: string
}

export interface RoomVO {
  id: number
  roomNumber: string
  floorId: number
  roomTypeName: string
  roomTypeIcon: string
  status: RoomStatus
  assignment: RoomAssignmentVO | null
}

export interface RoomBoardVO {
  floors: FloorVO[]
  statusSummary: Record<string, number>
  occupancyRate: number
  rooms: RoomVO[]
}

export interface RoomStatusLogVO {
  id: number
  previousStatus: string
  newStatus: string
  operatorName: string
  reason: string
  createdAt: string
}

export interface RoomStatusChangeDTO {
  newStatus: string
  reason?: string
}

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}
