this.$.scroller.addEventListener('scroll', (scrollEvent) => {requestAnimationFrame(
    () => {if(this.$.table.scrollTop / (this.$.table.scrollHeight - this.$.table.clientHeight) >= 0.95){
    $0.$server.onGridEnd()}}
)},true)