const sortNode = (a, b) => (a.sort || 0) - (b.sort || 0) || a.id - b.id

export const buildCategoryTree = (list) => {
  const map = new Map()
  ;(list || []).forEach((c) => map.set(c.id, { ...c, children: [] }))
  const roots = []
  map.forEach((node) => {
    const pid = node.parentId || 0
    if (pid === 0 || !map.has(pid)) {
      roots.push(node)
    } else {
      map.get(pid).children.push(node)
    }
  })
  const walk = (nodes, level) => {
    nodes.sort(sortNode)
    nodes.forEach((n) => {
      n.level = level
      walk(n.children, level + 1)
    })
  }
  walk(roots, 1)
  return roots
}

export const flattenCategoryTree = (tree) => {
  const out = []
  const walk = (nodes) => {
    nodes.forEach((n) => {
      out.push(n)
      walk(n.children)
    })
  }
  walk(tree || [])
  return out
}

export const categorySelectOptions = (list) =>
  flattenCategoryTree(buildCategoryTree(list)).map((n) => ({
    id: n.id,
    label: '　'.repeat(n.level - 1) + n.name,
    level: n.level
  }))
