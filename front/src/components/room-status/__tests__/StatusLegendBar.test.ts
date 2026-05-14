import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import StatusLegendBar from '../StatusLegendBar.vue'

describe('StatusLegendBar', () => {
  const summary = {
    CLEAN: 5,
    RESERVED: 2,
    BOOKED: 3,
    OCCUPIED: 8,
    DIRTY: 1,
    OUT_OF_SERVICE: 1,
  }

  it('renders all status labels', () => {
    const wrapper = mount(StatusLegendBar, {
      props: { summary },
    })
    expect(wrapper.text()).toContain('空净')
    expect(wrapper.text()).toContain('预留')
    expect(wrapper.text()).toContain('预订')
    expect(wrapper.text()).toContain('在住')
    expect(wrapper.text()).toContain('脏房')
    expect(wrapper.text()).toContain('停售')
  })

  it('renders correct counts', () => {
    const wrapper = mount(StatusLegendBar, {
      props: { summary },
    })
    expect(wrapper.text()).toContain('空净(5)')
    expect(wrapper.text()).toContain('在住(8)')
    expect(wrapper.text()).toContain('脏房(1)')
  })

  it('shows 0 for missing status', () => {
    const wrapper = mount(StatusLegendBar, {
      props: { summary: { CLEAN: 3 } },
    })
    expect(wrapper.text()).toContain('在住(0)')
  })
})
