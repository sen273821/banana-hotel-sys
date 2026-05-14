import { RoomStatus } from '../types/room'

export interface RoomStatusConfig {
  label: string
  color: string
  bgColor: string
  icon: string
}

export const ROOM_STATUS_CONFIG: Record<RoomStatus, RoomStatusConfig> = {
  [RoomStatus.CLEAN]: {
    label: '空净',
    color: '#16a34a',
    bgColor: '#dcfce7',
    icon: 'check_circle',
  },
  [RoomStatus.RESERVED]: {
    label: '预留',
    color: '#f59e0b',
    bgColor: '#fef3c7',
    icon: 'bookmark',
  },
  [RoomStatus.BOOKED]: {
    label: '预订',
    color: '#3b82f6',
    bgColor: '#dbeafe',
    icon: 'event',
  },
  [RoomStatus.OCCUPIED]: {
    label: '在住',
    color: '#7c3aed',
    bgColor: '#ede9fe',
    icon: 'person',
  },
  [RoomStatus.DIRTY]: {
    label: '脏房',
    color: '#dc2626',
    bgColor: '#fee2e2',
    icon: 'cleaning_services',
  },
  [RoomStatus.OUT_OF_SERVICE]: {
    label: '停售',
    color: '#6b7280',
    bgColor: '#f3f4f6',
    icon: 'block',
  },
}

export const ROOM_STATUS_TRANSITIONS: Record<RoomStatus, RoomStatus[]> = {
  [RoomStatus.CLEAN]: [
    RoomStatus.RESERVED,
    RoomStatus.BOOKED,
    RoomStatus.OCCUPIED,
    RoomStatus.OUT_OF_SERVICE,
  ],
  [RoomStatus.RESERVED]: [RoomStatus.CLEAN, RoomStatus.OUT_OF_SERVICE],
  [RoomStatus.BOOKED]: [RoomStatus.OCCUPIED, RoomStatus.CLEAN],
  [RoomStatus.OCCUPIED]: [RoomStatus.DIRTY],
  [RoomStatus.DIRTY]: [RoomStatus.CLEAN],
  [RoomStatus.OUT_OF_SERVICE]: [RoomStatus.CLEAN],
}
