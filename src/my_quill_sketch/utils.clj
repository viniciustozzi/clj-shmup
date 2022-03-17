(ns my-quill-sketch.utils)

(defn btn-pressed?
  "Checks if a button is being pressed, receives a keyword button
  such as ':up' or ':left' and a vector of inputs
  (probably :input from global state)
  Returns the pressed button"
  [btn inputs]
  (some #{btn} inputs))

(defn conj-once
  "Adds a value to a collection if the value is not yet there.
  This function checks if the value already exists in the collection
  before adding.
  Returns new collection with item added (or not)"
  [coll val]
  (if (not (some #{val} coll))
    (conj coll val)
    coll))
