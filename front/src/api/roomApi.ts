import http from './http'
import type { RoomBoardVO, RoomStatusChangeDTO, RoomStatusLogVO } from '../types/room'

export function fetchRoomBoard(floorId?: number) {
  const params = floorId ? { floorId } : {}
  return http.get<RoomBoardVO>('/api/v1/rooms/board', { params })
}

export function changeRoomStatus(roomId: number, dto: RoomStatusChangeDTO) {
  return http.put<void>(`/api/v1/rooms/${roomId}/status`, dto)
}

export function fetchRoomLogs(roomId: number, page = 1, size = 20) {
  return http.get<RoomStatusLogVO[]>(`/api/v1/rooms/${roomId}/logs`, {
    params: { page, size },
  })
}
