var prepare = function (model) {
    var varr = {}
    if (model == null) return
    model.vertices.forEach(function(v) { 
        varr[v.id] = v
        v.edges = []        
        system.addNode(v.id, v)
    })

    model.edges.forEach(function(v) {
        varr[v.from].edges.push(v)      
        system.addEdge(v.from, v.to, v)
    })

    var wordlevel = model.vertices.reduce(function(p, c) { if (c.type == "WordFormToken") p.push(c); return p }, [])
    wordlevel.forEach(function(e) { e.level = 0 })

    var nlvl = wordlevel
        level = -1

    while (nlvl.length > 0) {
        nlvl = nlvl.reduce(function(p, c) {
            c.edges.forEach(function (e) { 
                if (e.type == "child") p.push(varr[e.to]) 
            })
            return p
        }, [])

        nlvl.forEach(function (e) { e.level = level })
        level -= 1
    }

    level = 1
    nlvl = wordlevel

    while (nlvl.length > 0) {
        nlvl = nlvl.reduce(function(p, c) {
            c.edges.forEach(function (e) {
                if (e.type == "parent") p.push(varr[e.to]) 
            })
            return p
        }, [])

        nlvl.forEach(function (e) { e.level = level })
        level += 1
    }    
}
