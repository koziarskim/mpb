export const CustomMask = {
  bind(el, binding, vnode) {
    bind(el, binding, vnode);
  },
  //This is used for dynamic mask change.
  update(el, binding, vnode) {
    if (binding.value.toString() == binding.oldValue.toString()) return;
    el.removeEventListener("input", vnode.context.customMask);
    bind(el, binding, vnode);
  }
};
function bind(el, binding, vnode) {
  el.binding = binding;
  el.addEventListener("blur", onBlur);
  if (vnode.data.on && vnode.data.on.input)
    el.removeEventListener("input", vnode.data.on.input);
  let maskFunc = initMask(el, binding);
  el.addEventListener("input", maskFunc);
  vnode.context.customMask = maskFunc;
  if (vnode.data.on && vnode.data.on.input)
    el.addEventListener("input", vnode.data.on.input);
  el.dispatchEvent(new Event("input"));
}
function onBlur(event) {
  console.log("testing blur " + event.srcElement.isValid);
  if (!event.srcElement.binding.value.test(event.srcElement.value)) {
    event.srcElement.classList.add("invalid");
  } else {
    event.srcElement.classList.remove("invalid");
  }
}
function initMask(el, val) {
  let frame = [];
  let str = val.value.toString().slice(1, -1);
  let reg = /(?:((?:\\.)|(?:\[.+?\]))\{[\d,]+\})|\\.|./g,
    match;
  while ((match = reg.exec(str))) {
    let toPush = {};
    if (match[0].length <= 2) {
      toPush.minLen = 1;
      toPush.maxLen = 1;
      toPush.type = "single";
      toPush.char = match[0][match[0].length - 1];
      toPush.reg = new RegExp(match[0] + "+");
    } else {
      toPush.minLen = +/\{(\d+)/.exec(match[0])[1];
      toPush.maxLen = +/(\d+)\}/.exec(match[0])[1];
      toPush.reg = new RegExp("(_|" + match[1] + ")+");
    }
    frame.push(toPush);
  }
  return function() {
    if (this.value === "" && this.placeholder) {
      return;
    }

    let arr = this.value.split("").map(e => {
      return { char: e, type: "char" };
    });
    let pos = { char: "", type: "pos" };
    arr.splice(get(this), 0, pos);
    //TODO: Are you sure this works?
    let newVal = [];
    mainLoop: for (let i = 0; i < frame.length; i++) {
      for (let k = 0; k < frame[i].maxLen; k++) {
        if (arr[0] && arr[0].type == "pos") {
          newVal.push(arr.shift());
          k--;
          continue;
        }
        if (arr[0] == undefined) {
          if (frame[i].type == "single") {
            newVal.push({ char: frame[i].char });
          } else {
            if (k >= frame[i].minLen) {
              continue mainLoop;
            }
            newVal.push({ char: "_" });
          }
          continue;
        }
        if (frame[i].reg.exec(arr[0].char)) {
          if (k >= frame[i].minLen && arr[0].char == "_") {
            continue mainLoop;
          }
          newVal.push(arr.shift());
        } else {
          if (frame[i].type == "single") {
            newVal.push({ char: frame[i].char });
          } else {
            if (k >= frame[i].minLen) {
              continue mainLoop;
            }
            arr.shift();
            k--;
          }
        }
      }
    }
    this.value = newVal.map(e => e.char).join("");
    set(this, newVal.indexOf(pos));
  };
}
function get(input) {
  var position = 0;
  if (input.selectionStart || input.selectionStart == 0) {
    // Standart.
    position = input.selectionStart;
  } else if (document.selection) {
    // Legacy IE
    var Sel = document.selection.createRange();
    Sel.moveStart("character", -input.value.length);
    position = Sel.text.length;
  }
  return position;
}
function set(input, pos) {
  if (pos === -1) {
    input.classList.add("invalid");
    input.isValid = false;
  } else {
    input.classList.remove("invalid");
    input.isValid = true;
  }
  if (input.setSelectionRange) {
    input.setSelectionRange(pos, pos);
  } else if (input.createTextRange) {
    var range = input.createTextRange();
    range.collapse(true);
    range.moveEnd("character", pos);
    range.moveStart("character", pos);
    range.select();
  }
}
