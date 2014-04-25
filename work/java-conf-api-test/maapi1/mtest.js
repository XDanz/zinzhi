var validate_int = {
  validate: function(ctx, path, val) {
    if (val > 100)
      return ["warning","must be less than 100"];
    else
      return true;
  }
};
