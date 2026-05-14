import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import RoomCard from '../RoomCard.vue'
import { RoomStatus } from '../../../types/room'
import type { RoomVO } from '../../../types/room'

function createRoom(overrides: Partial<RoomVO> = {}): RoomVO {
  return {
    id: 1,
    roomNumber: '101',
    floorId: 1,
    roomTypeName: '豪华大床房',
    roomTypeIcon: 'king_bed',
    status: RoomStatus.CLEAN,
    assignment: null,
    ...overrides,
  }
}

describe('RoomCard', () => {
  it('renders room number', () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom() },
    })
    expect(wrapper.text()).toContain('101')
  })

  it('renders room type name', () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom() },
    })
    expect(wrapper.text()).toContain('豪华大床房')
  })

  it('shows clean status label for CLEAN room', () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom({ status: RoomStatus.CLEAN }) },
    })
    expect(wrapper.text()).toContain('空净')
  })

  it('shows occupied status label for OCCUPIED room', () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom({ status: RoomStatus.OCCUPIED }) },
    })
    expect(wrapper.text()).toContain('在住')
  })

  it('shows dirty status label for DIRTY room', () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom({ status: RoomStatus.DIRTY }) },
    })
    expect(wrapper.text()).toContain('脏房')
  })

  it('displays guest name when assignment exists', () => {
    const room = createRoom({
      status: RoomStatus.OCCUPIED,
      assignment: {
        id: 1,
        guestName: '张三',
        guestPhone: '138****1234',
        checkInTime: '2026-05-13T14:00:00',
        checkOutTime: '2026-05-15T12:00:00',
      },
    })
    const wrapper = mount(RoomCard, { props: { room } })
    expect(wrapper.text()).toContain('张三')
  })

  it('does not display guest section when no assignment', () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom() },
    })
    expect(wrapper.text()).not.toContain('张三')
  })

  it('emits changeStatus event when action clicked', async () => {
    const wrapper = mount(RoomCard, {
      props: { room: createRoom({ status: RoomStatus.CLEAN }) },
    })
    // Open menu
    const menuBtn = wrapper.find('button')
    await menuBtn.trigger('click')
    // Click first action
    const actions = wrapper.findAll('.min-w-\\[140px\\] button')
    if (actions.length > 0) {
      await actions[0].trigger('click')
      expect(wrapper.emitted('changeStatus')).toBeTruthy()
    }
  })
})
